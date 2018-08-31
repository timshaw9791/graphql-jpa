package org.crygier.graphql.wechatpay.model;

import okhttp3.RequestBody;
import org.crygier.graphql.wechatpay.model.wechat.response.WeChatPayRefundResponse;
import org.crygier.graphql.wechatpay.model.wechat.response.WeChatPaySyncResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Curtain
 * @date 2018/8/30 15:30
 */
public interface WeChatPayApi {
    /**
     * 统一下单
     * @param body
     * @return
     */
    @POST("/templates/pay/unifiedorder")
    Call<WeChatPaySyncResponse> unifiedOrder(@Body RequestBody body);

    /**
     * 申请退款
     * @param body
     * @return
     */
    @POST("/secapi/templates.pay/refund")
    Call<WeChatPayRefundResponse> refund(@Body RequestBody body);

}
