package org.crygier.graphql.mlshop.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.crygier.graphql.mlshop.service.VerificationService;
import org.crygier.graphql.mlshop.util.AliyunMessageUtil;
import org.crygier.graphql.mlshop.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.crygier.graphql.mlshop.util.VerifyUtil.*;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String getCode(String number, Integer type) {

        String randomNum = NumberUtil.createRandomNum(6);
        String jsonContent = "{\"code\":\"" + randomNum + "\"}";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneNumber", number);
        paramMap.put("jsonContent", jsonContent);
        paramMap.put("templateCode", "SMS_141765077");
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            throw new RuntimeException("Get verify code fail.");
        }
        switch (type) {
            case 1:
                redisTemplate.opsForValue().set(number + REGISTER, randomNum);
                break;
            case 2:
                redisTemplate.opsForValue().set(number + MODIFY_PASSWORD, randomNum);
                break;
            case 3:
                redisTemplate.opsForValue().set(number + CONSULT, randomNum);
                break;
            case 4:
                redisTemplate.opsForValue().set(number + MODIFY_PHONE, randomNum);
                break;
            case 5:
                redisTemplate.opsForValue().set(number + BARGAIN, randomNum);
                break;
            default:
                throw new RuntimeException("Get verify code fail. type not true");
        }
        redisTemplate.opsForValue().set(number, randomNum);
        return randomNum;
    }

    @Override
    public void visitCode(String phone, String code) {
        String jsonContent = "{\"code\":\"" + code + "\"}";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneNumber", phone);
        paramMap.put("jsonContent", jsonContent);
        paramMap.put("templateCode", "SMS_142384656");
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            throw new RuntimeException("Get verify code fail.");
        }
    }

    @Override
    public String verify(String code, String number, Integer type) {

        switch (type) {
            case 1:
                verifycode(code, number + REGISTER);
                break;
            case 2:
                verifycode(code, number + MODIFY_PASSWORD);
                break;
            case 3:
                verifycode(code, number + CONSULT);
                break;
            case 4:
                verifycode(code, number + MODIFY_PHONE);
                break;
            case 5:
                verifycode(code, number + BARGAIN);
                break;
            default:
                throw new RuntimeException("verify code fail. type not true");
        }

        return "OK";

    }

    private void verifycode(String code, String key) {
        String rs = String.valueOf(redisTemplate.opsForValue().get(key));
        if (rs.equals(code)) {
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
        } else {
            throw new RuntimeException("Verify code fail,please try to get the verification code.");
        }

    }


}

