package com.dili.card.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用于持有支付相关参数
 * @author xuliang
 */
@Component
@ConfigurationProperties(prefix = "pay")
public class PayProperties {

    /** 支付系统域名*/
    private String domain;
    /** 应用ID*/
    private long appId;
    /** 秘钥*/
    private String secret;
    /** token*/
    private String token;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}