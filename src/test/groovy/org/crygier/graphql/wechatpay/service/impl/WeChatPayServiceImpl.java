package org.crygier.graphql.wechatpay.service.impl;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.crygier.graphql.wechatpay.config.SignType;
import org.crygier.graphql.wechatpay.config.WeChatPayConfig;
import org.crygier.graphql.wechatpay.constants.WeChatPayConstants;
import org.crygier.graphql.wechatpay.model.WeChatPayApi;
import org.crygier.graphql.wechatpay.model.request.PayRequest;
import org.crygier.graphql.wechatpay.model.request.RefundRequest;
import org.crygier.graphql.wechatpay.model.response.PayResponse;
import org.crygier.graphql.wechatpay.model.response.RefundResponse;
import org.crygier.graphql.wechatpay.model.wechat.request.WeChatPayRequest;
import org.crygier.graphql.wechatpay.model.wechat.response.WeChatPaySyncResponse;
import org.crygier.graphql.wechatpay.service.WeChatPayService;
import org.crygier.graphql.wechatpay.utils.RandomUtil;
import org.crygier.graphql.wechatpay.utils.SignatureUtil;
import org.crygier.graphql.wechatpay.utils.XmlUtil;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Curtain
 * @date 2018/8/30 15:48
 */
public class WeChatPayServiceImpl implements WeChatPayService {

    private WeChatPayConfig weChatPayConfig;

    public void setWeChatPayConfig(WeChatPayConfig weChatPayConfig) {
        this.weChatPayConfig = weChatPayConfig;
    }

    @Override
    public PayResponse pay(PayRequest request) {
        return null;
    }

    @Override
    public PayResponse h5pay(PayRequest request) {

        WeChatPayRequest weChatPayRequest = new WeChatPayRequest();
        weChatPayRequest.setOutTradeNo(request.getOrderId());
        weChatPayRequest.setTotalFee(request.getOrderAmount());
        weChatPayRequest.setBody(request.getOrderName());
        weChatPayRequest.setSceneInfo(request.getSceneInfo());
        weChatPayRequest.setSpbillCreateIp(request.getSpbillCreateIp());

        weChatPayRequest.setTradeType("MWEB");
        weChatPayRequest.setAppid(weChatPayConfig.getAppId());
        weChatPayRequest.setMchId(weChatPayConfig.getMchId());
        weChatPayRequest.setNotifyUrl(weChatPayConfig.getNotifyUrl());
        weChatPayRequest.setNonceStr(RandomUtil.getRandomStr());

        weChatPayRequest.setSign(SignatureUtil.sign(buildMap(weChatPayRequest), weChatPayConfig.getMchKey()));


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeChatPayConstants.WE_CHAT_PAY_BASE_URL)
                //xml转化器   SimpleXmlConverterFactory 需要另外添加依赖  不在retrofit2中
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        String xml = XmlUtil.toXMl(weChatPayRequest);
        System.out.println(xml);
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),xml);
        Call<WeChatPaySyncResponse> call = retrofit.create(WeChatPayApi.class).unifiedOrder(body);
        Response<WeChatPaySyncResponse> retrofitResponse  = null;
        try{
            retrofitResponse = call.execute();
            System.out.println(retrofitResponse.body());
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信统一支付】发起支付, 网络异常");
        }
        WeChatPaySyncResponse response = retrofitResponse.body();
//        log.info("【微信统一支付】response={}", JsonUtil.toJson(response));

        if(!response.getReturnCode().equals("SUCCESS")) {
            throw new RuntimeException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals("SUCCESS")) {
            throw new RuntimeException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }

        return buildH5PayResponse(response);
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return false;
    }

    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public PayResponse asyncNotify(String notifyData) {
        return null;
    }

    @Override
    public RefundResponse refund(RefundRequest request) {
        return null;
    }


    /**
     * 构造map
     * @param weChatPayRequest
     * @return
     */
    private Map<String, String> buildMap(WeChatPayRequest weChatPayRequest) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatPayRequest.getAppid());
        map.put("mch_id", weChatPayRequest.getMchId());
        map.put("nonce_str", weChatPayRequest.getNonceStr());
        map.put("sign", weChatPayRequest.getSign());
        map.put("body", weChatPayRequest.getBody());
        map.put("notify_url", weChatPayRequest.getNotifyUrl());
        map.put("out_trade_no", weChatPayRequest.getOutTradeNo());
        map.put("spbill_create_ip", weChatPayRequest.getSpbillCreateIp());
        map.put("total_fee", String.valueOf(weChatPayRequest.getTotalFee()));
        map.put("trade_type", weChatPayRequest.getTradeType());
        map.put("scene_info",weChatPayRequest.getSceneInfo());

        return map;
    }

    /**
     * 返回给h5的参数
     *
     * @param response
     * @return
     */
    private PayResponse buildH5PayResponse(WeChatPaySyncResponse response) {
        //跳转的url
        PayResponse payResponse = new PayResponse();
        payResponse.setMwebUrl(response.getMwebUrl());

        return payResponse;
    }

}
