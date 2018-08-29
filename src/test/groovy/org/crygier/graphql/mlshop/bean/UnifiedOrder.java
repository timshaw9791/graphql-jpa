package org.crygier.graphql.mlshop.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * @author Curtain
 * @date 2018/8/29 9:51
 */
@Data
@XStreamAlias("xml")
public class UnifiedOrder {
    /**
     * 公众账号ID
     */
    private String appid;
    /**
     * 商户号
     */
    @XStreamAlias("mch_id")
    private String mchId;
    /**
     * 随机串
     */
    @XStreamAlias("nonce_str")
    private String nonceStr;
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
    @XStreamAlias("notify_url")
    private String notifyUrl;
    /**
     * 商户订单号
     */
    @XStreamAlias("out_trade_no")
    private String outTradeNo;
    /**
     * 终端IP（用户）
     */
    @XStreamAlias("spbill_create_ip")
    private String spbillCreateIp;
    /**
     * 总金额
     */
    @XStreamAlias("total_fee")
    private Integer totalFee;
    /**
     * 交易类型
     */
    @XStreamAlias("trade_type")
    private String tradeType;
    /**
     * 统一下单接口
     */
    @XStreamAlias("prepay_id")
    private String prepayId;
    /**
     * 信息
     */
    @XStreamAlias("scene_info")
    private String sceneInfo;
}
