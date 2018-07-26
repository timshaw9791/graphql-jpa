package org.crygier.graphql.mlshop.model;

import cn.wzvtcsoft.x.bos.domain.Entry;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.SchemaDocumentation;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author Curtain
 * @date 2018/7/26 8:58
 */

@Entity
@SchemaDocumentation("买车沟通记录")
@CompileStatic
public class CarCommunicationRecord extends Entry {

    @SchemaDocumentation("业务员")
    @ManyToOne
    Salesman salesman;

    @SchemaDocumentation("沟通时间")
    long communicateTime;

    @SchemaDocumentation("关注车型")
    String carType;

    @SchemaDocumentation("分配时间")
    long distributeTime;

    @SchemaDocumentation("沟通记录")
    String record;

    /*客户信息中的客户等级  随着记录的变化而变化*/
    @SchemaDocumentation("客户级别：分为ABC三等")
    CarSourceTypeEnum level;

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

