package org.crygier.graphql.wechatpay.model.request;

import lombok.Data;
import org.crygier.graphql.wechatpay.enums.WeChatPayTypeEnum;

/**
 * 退款时请求参数
 * @author Curtain
 * @date 2018/8/30 15:05
 */
@Data
public class RefundRequest {
    /**
     * 支付方式.
     */
    private WeChatPayTypeEnum payTypeEnum;

    /**
     * 订单号.
     */
    private String orderId;

    /**
     * 订单金额.
     */
    private Long orderAmount;
}
