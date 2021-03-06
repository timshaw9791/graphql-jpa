package org.crygier.graphql;

import cn.wzvtcsoft.x.bos.domain.BosEntity;
import cn.wzvtcsoft.x.bos.domain.BosEnum;
import cn.wzvtcsoft.x.bos.domain.Bostype;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

@SchemaDocumentation("过滤条件")
public class QueryFilter {

    @SchemaDocumentation("键，可以带导航的.号")
    public void setKey(String key) {
        this.key = key;
    }
    @SchemaDocumentation("值，可以是和like相对应的%abc%")
    public void setValue(String value) {
        this.value = value;
    }
    @SchemaDocumentation("操作符")
    public void setOperator(QueryFilterOperator operator) {
        this.operator = operator;
    }
    @SchemaDocumentation("条件组合符号")
    public void setCombinator(QueryFilterCombinator combinator) {
        this.combinator = combinator;
    }
    @SchemaDocumentation("下一个条件")
    public void setNext(QueryFilter next) {
        this.next = next;
    }


    private String key;

    private String value;

    private QueryFilterOperator operator;

    private QueryFilterCombinator combinator;

    private QueryFilter next;

    public QueryFilter(){

    }
    public QueryFilter(String key, QueryFilterOperator operator, String value, QueryFilterCombinator combinator, QueryFilter next) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.combinator = combinator;
        this.next = next;
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public QueryFilterOperator getOperator() {
        return operator;
    }

    public QueryFilterCombinator getCombinator() {
        return combinator;
    }

    public QueryFilter getNext() {
        return next;
    }

}

//TODO 可能需要扩展或者更规范化

@SchemaDocumentation("查询过滤操作符")
enum QueryFilterOperator implements BosEnum {
    ISNULL("ISNULL","为空","is null"),
    ISNOTNULL("ISNOTNULL","不为空","is not null"),
    GREATTHAN("GREATTHAN","大于",">"),
    LESSTHAN("LESSTHAN","小于","<"),
    NOTLESSTHAN("NOTLESSTHAN","不小于",">="),
    NOTGREATTHAN("NOTGREATTHAN","不大于","<="),
    EQUEAL("EQUEAL","相等","="),
    IN("IN","包含","in"),
    NOTIN("NOTIN","不包含","not in"),
    NOT("NOT","非","not"),
    LIKE("LIKE","LIKE","like");

    private QueryFilterOperator(String value,String name,String description){
        this.ev = new BosEnum.EnumInnerValue(value, name, description);
    }

    private BosEnum.EnumInnerValue ev = null;

    @Override
    public EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }
}



@SchemaDocumentation("查询表达式组合操作符")
enum QueryFilterCombinator implements BosEnum {
    AND("AND","and","并且的意思"),
    OR("OR","or","或者的意思"),
    NOT("NOT","!","取反");

    private QueryFilterCombinator(String value,String name,String description){
        this.ev = new BosEnum.EnumInnerValue(value, name, description);
    }
    private BosEnum.EnumInnerValue ev = null;
    @Override
    public BosEnum.EnumInnerValue getEnumInnerValue() {
        return this.ev;
    }
}
