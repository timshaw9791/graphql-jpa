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

    private final String REGISTER = "register";

    private final String MODIFY_PASSWORD = "modifyPassword";

    private final String CONSULT = "consult";

    private final String MODIFY_PHONE = "modifyPhone";

    @Override
    public String getCode(String number, Integer type) {

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
            default:
                throw new RuntimeException("Get verify code fail. type not true");
        }
        redisTemplate.opsForValue().set(number, randomNum);
        return randomNum;
    }

    @Override
    public String verify(String code, String number, Integer type) {


        String rs = (String) redisTemplate.opsForValue().get(number);
        if (rs.equals(code)) {
            redisTemplate.delete(number);
            return "OK";
        }
        throw new RuntimeException("fail");

    }


}

