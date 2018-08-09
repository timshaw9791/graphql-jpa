package org.crygier.graphql;

import org.hibernate.Session;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.Loader;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PaginatorFactory {
    private final EntityType entityType;
    private final EntityManager entityManager;
    private final SessionFactoryImplementor factory;
    private final SharedSessionContractImplementor sharedSessionContractImplementor;

    public PaginatorFactory(EntityManager entityManager, EntityType entityType) {
        this.entityType = entityType;
        this.entityManager = entityManager;
        this.factory = ((SessionFactoryImplementor) entityManager.unwrap(Session.class).getSessionFactory());
        this.sharedSessionContractImplementor = ((SharedSessionContractImplementor) entityManager.unwrap(Session.class));
    }

    public List<String> getPaginator(TypedQuery typedQuery, Paginator pageInformation) {
        //组建sql1:并准备where子句中的查询参数
        QueryTranslatorImpl queryTranslatorImpl = getQueryTranslator(typedQuery);
        QueryTranslator paginatorTemplateQueryTranslator = getPaginatorTemplateQueryTranslator(pageInformation);
        //组建sql2。并准备分页参数
        //组合两个sql并整合参数
        //ParameterTranslations parameterTranslations = queryTranslator.getParameterTranslations();
        RowSelection rowSelection = new RowSelection();
        rowSelection.setFirstRow((pageInformation.getPage() - 1) * pageInformation.getSize());
        rowSelection.setMaxRows(pageInformation.getSize());
        //组建sql2。并准备分页参数

        LimitHandler limitHandler = this.factory.getServiceRegistry().getService(JdbcServices.class).getDialect().getLimitHandler();
        //组合两个sql并整合参数
        String composedNativeSql = composeIdPaginationNativeSql(queryTranslatorImpl.getSQLString(), limitHandler.processSql(paginatorTemplateQueryTranslator.getSQLString(), rowSelection));
        final QueryParameters queryParameters = ((org.hibernate.query.internal.QueryImpl) typedQuery.unwrap(QueryImplementor.class)).getQueryParameters().createCopyUsing(rowSelection);
        //预编译
        PreparedStatement st = sharedSessionContractImplementor.getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
                composedNativeSql,
                false,
                null
        );
        try {
            //绑定到PreparedStatement  //绑定limit
            int col = 1;
            col += limitHandler.bindLimitParametersAtStartOfQuery(rowSelection, st, col);
            col += bindParameterValues(st, queryParameters, col, queryTranslatorImpl);
            col += limitHandler.bindLimitParametersAtEndOfQuery(rowSelection, st, col);
            limitHandler.setMaxRows(rowSelection, st);
            ResultSet rs = sharedSessionContractImplementor.getJdbcCoordinator().getResultSetReturn().extract(st);
            List<String> idList = new ArrayList<>();
            final int maxRows = LimitHelper.hasMaxRows(rowSelection) ?
                    rowSelection.getMaxRows() :
                    Integer.MAX_VALUE;
            for (int count = 0; count < maxRows && rs.next(); count++) {
                String result = rs.getString(1);
                idList.add(result);
            }
            return idList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PaginatorFactory.getPaginator...");
        } finally {
            this.sharedSessionContractImplementor.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
            this.sharedSessionContractImplementor.getJdbcCoordinator().afterStatementExecution();
        }
    }

    private String composeIdPaginationNativeSql(String innerGqlString, String outerString) {
        List orderbyColumns = new ArrayList<>();
        int orderbyIdx = innerGqlString.toUpperCase().indexOf(" ORDER BY");
        if (orderbyIdx > 0) {
            orderbyColumns.addAll(Arrays.stream(innerGqlString.substring(orderbyIdx + 9).split(",")).map(str -> {
                        int ascidx = str.toUpperCase().indexOf("ASC");
                        if (ascidx > 0) {
                            return str.substring(0, ascidx);
                        }
                        int descidx = str.toUpperCase().indexOf("DESC");
                        if (descidx > 0) {
                            return str.substring(0,descidx);
                        }
                        return str;
                    }
            ).collect(Collectors.toList()));
        }
        String replaceforInnerSQL = "";
        if (orderbyColumns.size() > 0) {
            replaceforInnerSQL = "," + StringUtils.collectionToDelimitedString(orderbyColumns, ",");
        }

        String outertableailas=outerString.substring(outerString.toUpperCase().indexOf("DISTINCT")+9,outerString.toUpperCase().indexOf(".ID"));
        outerString=outerString.replace(outertableailas,"__bospaginator");
        int innerformidx=outerString.toUpperCase().indexOf("FROM")+5;
        outerString=outerString.substring(0,innerformidx)+outerString.substring(outerString.indexOf("__bospaginator",innerformidx));

        int innerfromidx = innerGqlString.toUpperCase().indexOf(" FROM ");
        int inneridaliasidx=innerGqlString.toUpperCase().indexOf(" AS ");
        String innertable = innerGqlString.substring(0, inneridaliasidx) + replaceforInnerSQL + " FROM " + innerGqlString.substring(innerfromidx + 5);
        int outerfromidx = outerString.toUpperCase().indexOf(" FROM ");
        return outerString.substring(0, outerfromidx) + " FROM (" + innertable + ") " + outerString.substring(outerfromidx + 5);
    }

    //TODO 单例模式
    public QueryTranslator getPaginatorTemplateQueryTranslator(Paginator pageInformation) {
        CriteriaQuery criteriaQuery = entityManager.getCriteriaBuilder().createQuery(String.class);
        Root root = criteriaQuery.from(entityType);
        root.alias("abc");
        criteriaQuery = criteriaQuery.select(root.get("id").alias("id"));
        TypedQuery pagableTypeQUery = entityManager.createQuery(criteriaQuery.distinct(true));
        pagableTypeQUery.setMaxResults(pageInformation.getSize());
        pagableTypeQUery.setFirstResult((pageInformation.getPage() - 1) * pageInformation.getSize());
        return getQueryTranslator(pagableTypeQUery);
    }

    //以便获取QueryTranslator.getSqlString()以及相关参数信息
    private QueryTranslatorImpl getQueryTranslator(TypedQuery typedQuery) {
        QueryImpl queryImpl = typedQuery.unwrap(org.hibernate.query.internal.QueryImpl.class);
        String innerGqlString = queryImpl.getQueryString();
        QueryTranslator translator = new ASTQueryTranslatorFactory().
                createQueryTranslator(innerGqlString, innerGqlString, Collections.EMPTY_MAP, factory, null);
        translator.compile(Collections.EMPTY_MAP, false);
        return (QueryTranslatorImpl) translator;
    }

    /**
     * copied and revised from hibernate-core-5.2.17.Final: org.hibernate.loader.hql.QueryLoader
     * We specifically override this method here, because in general we know much more
     * about the parameters and their appropriate bind positions here then we do in
     * our super because we track them explicitly here through the ParameterSpecification
     * interface.
     *
     * @param queryParameters The encapsulation of the parameter values to be bound.
     * @param startIndex      The position from which to start binding parameter values.
     * @return The number of JDBC bind positions actually bound during this method execution.
     * @throws SQLException Indicates problems performing the binding.
     */
    protected int bindParameterValues(
            final PreparedStatement statement,
            final QueryParameters queryParameters,
            final int startIndex,
            QueryTranslatorImpl queryTranslator) throws SQLException {
        int position = startIndex;
        List<ParameterSpecification> parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
        for (ParameterSpecification spec : parameterSpecs) {
            position += spec.bind(statement, queryParameters, this.sharedSessionContractImplementor, position);
        }
        return position - startIndex;
    }

    protected static final CoreMessageLogger LOG = CoreLogging.messageLogger(Loader.class);
    protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
}
