package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.bean.WxPaySyncResponse;

/**
 * @author Curtain
 * @date 2018/8/29 9:43
 */
public interface PayService {

    WxPaySyncResponse weChatPay();

}
