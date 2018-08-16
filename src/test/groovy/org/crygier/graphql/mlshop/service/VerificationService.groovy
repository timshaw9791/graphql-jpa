package org.crygier.graphql.mlshop.service

public interface VerificationService {

    /**
     * 获取验证码
     * @param number
     * @return
     */
    String getCode(String number,Integer type);

    /**
     * 验证
     * @param code
     * @param number
     */
    String verify(String code, String number,Integer type);
}
