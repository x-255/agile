package com.perf.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatWorkConfig {

    @Value("${CORP_ID}")
    private String corpId;

    @Value("${AGENT_ID}")
    private Integer agentId;

    @Value("${APP_SECRET}")
    private String appSecret;

    public String getCorpId() {
        return corpId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public String getAppSecret() {
        return appSecret;
    }
}
