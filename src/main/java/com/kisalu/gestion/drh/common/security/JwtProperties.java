package com.kisalu.gestion.drh.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Configuration JWT (equivalent .env PHP : JWT_SERVER_*). */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {


    private String secret;
    private long duration = 3600;
    private String algorithm = "HS256";
    private String accessSub = "AUTH";
    private String refreshSub = "REFRESH";

    public JwtProperties() {
    }

    public JwtProperties(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAccessSub() {
        return accessSub;
    }

    public void setAccessSub(String accessSub) {
        this.accessSub = accessSub;
    }

    public String getRefreshSub() {
        return refreshSub;
    }

    public void setRefreshSub(String refreshSub) {
        this.refreshSub = refreshSub;
    }
}
