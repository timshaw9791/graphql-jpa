package org.crygier.graphql.mlshop.service;

import okhttp3.RequestBody;
import org.crygier.graphql.mlshop.bean.WxPaySyncResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Curtain
 * @date 2018/8/29 14:22
 */
public interface WxPayApi {
    /**
     * 统一下单
     * @param body
     * @return
     */
    @POST("/pay/unifiedorder")
    Call<WxPaySyncResponse> unifiedorder(@Body RequestBody body);

    /**
     * 申请退款
     * @param body
     * @return
     */
    @POST("/secapi/pay/refund")
    Call<WxPaySyncResponse> refund(@Body RequestBody body);
}
