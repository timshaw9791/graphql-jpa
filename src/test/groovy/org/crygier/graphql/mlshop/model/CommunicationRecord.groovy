package org.crygier.graphql.mlshop.model

import cn.wzvtcsoft.x.bos.domain.Bostype
import cn.wzvtcsoft.x.bos.domain.Entry
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum
import org.crygier.graphql.mlshop.model.enums.CustomerLevelEnum

import javax.persistence.Entity
import javax.persistence.ManyToOne

/**
 * @author Curtain
 * @date 2018/7/26 8:58
 */

@Entity
@SchemaDocumentation("沟通记录详情")
@CompileStatic
@Bostype("A09")
public class CommunicationRecord extends Entry {

    @SchemaDocumentation("业务员")
    @ManyToOne
    @JsonIgnoreProperties(value=["hibernateLazyInitializer","handler","fieldHandler"])
    Salesman salesman;

    @SchemaDocumentation("沟通时间")
    Long communicateTime;

    @SchemaDocumentation("关注车型")
    String carType;

    @SchemaDocumentation("分配时间")
    Long distributeTime;

    @SchemaDocumentation("沟通记录")
    String record;

    /*客户信息中的客户等级  随着记录的变化而变化*/
    @SchemaDocumentation("客户级别：分为ABC三等")
    CustomerLevelEnum level;

    @SchemaDocumentation("状态：分为待分配,待回访，已回访，已转换，战败五种")
    CarCommunicationStatusEnum status;

    @SchemaDocumentation("主管")
    String director;

    @SchemaDocumentation("主管意见")
    String directorSuggestion;

    @SchemaDocumentation("查看时间")
    String watchTime;

    @SchemaDocumentation("分配人")
    @ManyToOne
    Administ administ;

}

