package org.crygier.graphql.mlshop.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 砍价设置
 *
 * @author Curtain
 * @date 2018/8/27 8:16
 */

@Getter
@Setter
public class BargainSetting {

    /*人数*/
     Integer number;

    /*金额 单位分*/
     Long amount;

    /*有效时间*/
     Long effectiveTime;

}
