package org.crygier.graphql.mlshop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.crygier.graphql.mlshop.bean.BargainSetting;
import org.crygier.graphql.mlshop.exception.MLShopRunTimeException;
import org.crygier.graphql.mlshop.model.BargainRecord;
import org.crygier.graphql.mlshop.model.Order;
import org.crygier.graphql.mlshop.repo.BargainRecordRepository;
import org.crygier.graphql.mlshop.service.BargainService;
import org.crygier.graphql.mlshop.service.OrderService;
import org.crygier.graphql.mlshop.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.crygier.graphql.mlshop.utils.VerifyUtil.BARGAIN;

/**
 * @author Curtain
 * @date 2018/8/27 8:26
 */

@Service
public class BargainServiceImpl implements BargainService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BargainRecordRepository bargainRecordRepository;

    @Override
    public BargainSetting saveBargainSetting(BargainSetting bargainSetting) {
        String key = "bargainSetting";
        String value = JSONObject.toJSONString(bargainSetting);
        redisTemplate.opsForValue().set(key, value);
        return bargainSetting;
    }

    @Override
    public BargainSetting updateBargainSetting(BargainSetting bargainSetting) {

        return saveBargainSetting(bargainSetting);
    }

    @Override
    public BargainRecord generate(String orderId) {

        //判断是否已经生成砍价记录
        Order order = orderService.findOne(orderId);
        Optional<BargainRecord> optional = bargainRecordRepository.findByOrder(order);

        //存在则直接返回
        if (optional.isPresent()) {
            return optional.get();
        }

        //获取砍价设置的内容
        BargainSetting bargainSetting = findBargainSetting();

        BargainRecord bargainRecord = new BargainRecord();

        //初始化砍价记录信息
        bargainRecord.setAmount(bargainSetting.getAmount());
        bargainRecord.setEffectiveTime(bargainSetting.getEffectiveTime());
        bargainRecord.setPeopleNumber(bargainSetting.getNumber());
        bargainRecord.setOrder(order);

        return bargainRecordRepository.save(bargainRecord);
    }

    @Override
    public BargainRecord findBargainRecord(String orderId) {

        Optional<BargainRecord> optional = bargainRecordRepository.findByOrder(orderService.findOne(orderId));
        if (optional.isPresent()){
            return optional.get();
        }else {
            throw new MLShopRunTimeException("砍价记录未找到");
        }
    }

    @Override
    public BargainRecord bargain(String phone, String orderId) {
        //验证是否通过
        if (VerifyUtil.validity(phone + BARGAIN)) {
            //判断此用户是否已经砍价

            Order order = orderService.findOne(orderId);
            BargainRecord bargainRecord = bargainRecordRepository.findByOrder(order).get();

            String chopPhone = bargainRecord.getChopPhone();

            if (chopPhone != null && chopPhone.contains(phone)) {
                throw new RuntimeException("你已经砍价过了");
            }

            //判断是否在砍价时间内
            long time = bargainRecord.getCreatetime() + bargainRecord.getEffectiveTime();
            if (System.currentTimeMillis() > time) {
                throw new RuntimeException("时间已到期，砍价已结束");
            }

            //设置砍价信息
            if (bargainRecord.getChopCount().equals(0)) {
                bargainRecord.setChopPhone(phone);
            } else {
                bargainRecord.setChopPhone(bargainRecord.getChopPhone() + "," + phone);
            }
            long amount = bargainRecord.getAmount() / bargainRecord.getPeopleNumber();
            bargainRecord.setChopAmount(bargainRecord.getChopAmount() + amount);

            bargainRecord.setChopCount(bargainRecord.getChopCount() + 1);

            //如果人数达到 则砍价成功
            if (bargainRecord.getPeopleNumber().equals(bargainRecord.getChopCount())) {
                order.setBargainSuccess(true);
                orderService.update(order);
            }
            return bargainRecordRepository.save(bargainRecord);
        }
        throw new RuntimeException("验证码过期，请重新验证");
    }

    @Override
    public BargainSetting findBargainSetting() {
        String bargain = (String) redisTemplate.opsForValue().get("bargainSetting");

        JSONObject jsonObject = (JSONObject) JSON.parse(bargain);
        BargainSetting result = JSONObject.toJavaObject(jsonObject, BargainSetting.class);

        return result;
    }
}
