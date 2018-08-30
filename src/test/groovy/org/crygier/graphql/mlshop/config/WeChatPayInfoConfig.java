package org.crygier.graphql.mlshop.config;

import org.crygier.graphql.wechatpay.config.WeChatPayConfig;
import org.crygier.graphql.wechatpay.service.WeChatPayService;
import org.crygier.graphql.wechatpay.service.impl.WeChatPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Curtain
 * @date 2018/8/30 16:00
 */
@Component
public class WeChatPayInfoConfig {

    @Autowired
    private WeChatAccountConfig accountConfig;

    @Bean
    public WeChatPayService weChatPayService(){
        WeChatPayServiceImpl weChatPayService =new WeChatPayServiceImpl();
        weChatPayService.setWeChatPayConfig(wxPayH5Config());
        return weChatPayService;
    }

    @Bean
    public WeChatPayConfig wxPayH5Config(){
        WeChatPayConfig chatPayConfig =new WeChatPayConfig();
        chatPayConfig.setAppId(accountConfig.getAppId());
        chatPayConfig.setAppSecret(accountConfig.getAppSecret());
        chatPayConfig.setMchId(accountConfig.getMchId());
        chatPayConfig.setMchKey(accountConfig.getMchKey());
        chatPayConfig.setKeyPath(accountConfig.getKeyPath());
        chatPayConfig.setNotifyUrl(accountConfig.getNotifyUrl());
        return chatPayConfig;
    }
}
