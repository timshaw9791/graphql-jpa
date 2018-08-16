package cn.wzvtcsoft.x.bos.domain.persist;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class EntryParentType  extends AbstractSingleColumnStandardBasicType<CoreObject>
        implements DiscriminatorType<CoreObject> {

    public static final CoreObject INSTANCE = new CoreObject();

    public EntryParentType() {
        super( VarcharTypeDescriptor.INSTANCE, CoreObjectTypeDescriptor.INSTANCE );
    }

    @Override
    public CoreObject stringToObject(String xml) throws Exception {
        return fromString( xml );
    }

    @Override
    public String objectToSQLString(CoreObject value, Dialect dialect) throws Exception {
        return toString( value );
    }

    @Override
    public String getName() {
        return "bitset";
    }

}