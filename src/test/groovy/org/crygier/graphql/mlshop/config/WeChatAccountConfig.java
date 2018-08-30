package org.crygier.graphql.mlshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Curtain
 * @date 2018/8/30 15:58
 */
@Component
@Data
@ConfigurationProperties(prefix = "wechat")
public class WeChatAccountConfig {
    private String appId;

    private String appSecret;

    private String mchId;

    private String mchKey;

    private String keyPath;

    private String notifyUrl;

}
