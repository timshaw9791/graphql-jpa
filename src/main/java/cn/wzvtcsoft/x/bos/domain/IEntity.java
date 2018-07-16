package cn.wzvtcsoft.x.bos.domain;


/**
 * Created by liutim on 2017/11/25.
 */
public interface IEntity extends ICoreObject{

    public String getCreateactorid();

    public String getUpdateactorid();

    public long getCreatetime();

    public long getUpdatetime();
}

