package org.crygier.graphql.mlshop.service.impl

import okhttp3.MediaType
import okhttp3.RequestBody
import org.apache.commons.codec.digest.DigestUtils
import org.crygier.graphql.mlshop.bean.UnifiedOrder
import org.crygier.graphql.mlshop.bean.WxPaySyncResponse
import org.crygier.graphql.mlshop.service.PayService
import org.crygier.graphql.mlshop.service.WxPayApi
import org.crygier.graphql.mlshop.util.NumberUtil
import org.crygier.graphql.mlshop.util.StringUtils
import org.crygier.graphql.mlshop.util.XmlUtil
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

import javax.servlet.http.HttpServletRequest

/**
 * @author Curtain
 * @date 2018/8/29 9:49
 */

@Service
public class PayServiceImpl implements PayService {

    public static final String url = "https://api.mch.weixin.qq.com";

    @Override
    public WxPaySyncResponse weChatPay(HttpServletRequest request) {
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setAppid("wx5f6c931e05b2b39a");
        unifiedOrder.setMchId("1512065311");
        unifiedOrder.setNonceStr(NumberUtil.getRandomStr());
        unifiedOrder.setBody("猛龙出行汽车商城-定金支付");
        unifiedOrder.setOutTradeNo("201802300728012");
        unifiedOrder.setTotalFee(1);
        //todo 获取用户ip

        String ip = getIPAddress(request);

        unifiedOrder.setSpbillCreateIp("ip");
        unifiedOrder.setNotifyUrl("http://121.196.218.4:8080/mlsop/notify");
        unifiedOrder.setTradeType("MWEB");
        unifiedOrder.setSceneInfo("{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://www.menglongchuxing.cn\",\"wap_name\": \"猛龙出行\"}}");
        //最后再添加签名
        unifiedOrder.setSign(sign(buildMap(unifiedOrder),"BawUezX73SHVbo2AFZCbyK2Htq1ZZER7"));


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //xml转化器   SimpleXmlConverterFactory 需要另外添加依赖  不在retrofit2中
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        String xml = XmlUtil.toXMl(unifiedOrder);
        System.out.println(xml);
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),xml);
        Call<WxPaySyncResponse> call = retrofit.create(WxPayApi.class).unifiedorder(body);
        Response<WxPaySyncResponse> retrofitResponse  = null;
        try{
            retrofitResponse = call.execute();
            System.out.println(retrofitResponse.body());
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信统一支付】发起支付, 网络异常");
        }
        WxPaySyncResponse response = retrofitResponse.body();
//        log.info("【微信统一支付】response={}", JsonUtil.toJson(response));

        if(!response.getReturnCode().equals("SUCCESS")) {
            throw new RuntimeException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals("SUCCESS")) {
            throw new RuntimeException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }

        return response;

    }


    /**
     * 构造map
     * @param unifiedOrder
     * @return
     */
    private Map<String, String> buildMap(UnifiedOrder unifiedOrder) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", unifiedOrder.getAppid());
        map.put("mch_id", unifiedOrder.getMchId());
        map.put("nonce_str", unifiedOrder.getNonceStr());
        map.put("sign", unifiedOrder.getSign());
        map.put("body", unifiedOrder.getBody());
        map.put("notify_url", unifiedOrder.getNotifyUrl());
        map.put("out_trade_no", unifiedOrder.getOutTradeNo());
        map.put("spbill_create_ip", unifiedOrder.getSpbillCreateIp());
        map.put("total_fee", String.valueOf(unifiedOrder.getTotalFee()));
        map.put("trade_type", unifiedOrder.getTradeType());
        map.put("scene_info",unifiedOrder.getSceneInfo());

        return map;
    }

    /**
     * 签名
     * @param params
     * @param signKey
     * @return
     */
    public static String sign(Map<String, String> params, String signKey) {
        SortedMap<String, String> sortedMap = new TreeMap<>(params);

        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key);
            if (StringUtils.isNotEmpty(value) && !"sign".equals(key) && !"key".equals(key)) {
                toSign.append(key).append("=").append(value).append("&");
            }
        }

        toSign.append("key=").append(signKey);
        System.out.println(toSign.toString());
        return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
