package org.crygier.graphql.mlshop.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Curtain
 * @date 2018/8/29 9:51
 */
@Getter
@Setter
public class UnifiedOrder {
    /**
     * 公众账号ID
     */
    private String appid;
    /**
     * 商户号
     */
    private String mch_id;
    /**
     * 随机串
     */
    private String nonce_str;
    /**
     * 签名
     */
    private String sign;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 通知地址
     */
    private String notify_url;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 终端IP（用户）
     */
    private String spbill_create_ip;
    /**
     * 总金额
     */
    private Integer total_fee;
    /**
     * 交易类型
     */
    private String trade_type;
    /**
     * 统一下单接口
     */
    private String prepay_id;
    /**
     * 信息
     */
    private String scene_info;
}
