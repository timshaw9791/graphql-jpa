package org.crygier.graphql.mlshop.service;

import com.aliyuncs.exceptions.ClientException;

public interface VerificationService {

    /**
     * 获取验证码
     * @param number
     * @return
     */
    String getCode(String number);

    /**
     * 验证
     * @param code
     * @param number
     */
    String verify(String code, String number);
}
