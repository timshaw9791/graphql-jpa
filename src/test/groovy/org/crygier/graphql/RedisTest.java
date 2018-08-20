package org.crygier.graphql;

import com.aliyuncs.exceptions.ClientException;
import org.crygier.graphql.mlshop.config.ContextRefreshedEventListen;
import org.crygier.graphql.mlshop.service.StatisticService;
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

    @Autowired
    ContextRefreshedEventListen refreshedEventListen;

    @Autowired
    StatisticService statisticService;

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
        redisTemplate.opsForHash().put("carinfo","model","info");
        redisTemplate.opsForHash().put("carinfo","model2","info2");


        Object o = redisTemplate.opsForHash().get("carinfo", "model");
        System.out.println(o);
      o = redisTemplate.opsForHash().get("carinfo", "model2");
        System.out.println(o);

//        objects = redisTemplate.opsForHash().multiGet("keymap", objects);
        System.out.println(objects);

    }

    @Test
    public void sendmessage() throws ClientException {
//        verificationService.getCode("15395778303");
//        verificationService.getCode("18157726283");
    }

    @Test
    public void getRepository(){
//        Repository repository = refreshedEventListen.getRepository(Shop.class);
//        Optional shop = ((CrudRepository) repository).findById("ZJuMOSMXHgSrB3NNzpGKb2A05");
//        ((BosEntity)shop.get()).getCreatetime();

        List<Object> list = statisticService.allStatistic(0L, 40000000000000L);
        System.out.println(list);

    }
}
