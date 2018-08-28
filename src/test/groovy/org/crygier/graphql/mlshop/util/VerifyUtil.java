package org.crygier.graphql.mlshop.util;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Curtain
 * @date 2018/8/16 16:59
 */
public class VerifyUtil {

    public final static String REGISTER = "register";

    public final static String MODIFY_PASSWORD = "modifyPassword";

    public final static String CONSULT = "consult";

    public final static String MODIFY_PHONE = "modifyPhone";

    public final static String BARGAIN = "bargain";

    public static boolean validity(String key){
        RedisTemplate redisTemplate = (RedisTemplate) SpringUtil.getBean("redisTemplate");

        //判断验证是否过期  是否在一个小时内
        Long time = Long.valueOf((String) redisTemplate.opsForValue().get(key));
        if ((time+3600000)>System.currentTimeMillis()){
            return true;
        }
        return false;


    }
}
