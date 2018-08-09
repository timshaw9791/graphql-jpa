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

@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String getCode(String number){

        String randomNum = NumberUtil.createRandomNum(6);
        String jsonContent = "{\"code\":\"" + randomNum + "\"}";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phoneNumber", number);
        paramMap.put("jsonContent", jsonContent);
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            if (sendSmsResponse.getCode() == null) {
                throw new RuntimeException("fail");
            }
            if (!sendSmsResponse.getCode().equals("OK")) {
                throw new RuntimeException("fail");
            }
        }
        redisTemplate.opsForValue().set(number, randomNum);
        return randomNum;
    }

    @Override
    public String verify(String code, String number) {
        String rs = (String) redisTemplate.opsForValue().get(number);
        if (rs.equals(code)) {
            redisTemplate.delete(number);
            return "OK";
        }

        throw new RuntimeException("fail");

    }


}

