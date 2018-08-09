package org.crygier.graphql;

import com.aliyuncs.exceptions.ClientException;
import org.crygier.graphql.mlshop.service.VerificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Curtain
 * @date 2018/8/8 9:55
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    VerificationService verificationService;

    @Test
    public void add(){
        redisTemplate.opsForValue().set("key11","value11");
        Object test11 = redisTemplate.opsForValue().get("test11");
        System.out.println(test11);
    }

    @Test
    public void addList(){
        List<Object> strings = new ArrayList<Object>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        redisTemplate.opsForList().rightPush("listcollection4", strings);
//        redisTemplate.opsForList().leftPush("keylist99","aaa");

        List range = redisTemplate.opsForList().range("listcollection4", 0, -1);

        System.out.println(range);
    }

    @Test
    public void addHash(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("1","1");
        map.put("2",2);
        List<Object> objects = new ArrayList<>();
        objects.add("1");
        objects.add("2");

//        redisTemplate.opsForHash().putAll("keymap",map);

        objects = redisTemplate.opsForHash().multiGet("keymap", objects);
        System.out.println(objects);

    }

    @Test
    public void sendmessage() throws ClientException {
        verificationService.getCode("15395778303");
    }
}
