package com.perf.backend.util;

import com.perf.backend.config.WeChatWorkConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeChatWorkUtil {

    @Autowired
    private WeChatWorkConfig weChatWorkConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GET_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String GET_USER_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s";
    private static final String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserdetail?access_token=%s";

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REF = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    public String getAccessToken() {
        String url = String.format(GET_ACCESS_TOKEN_URL, weChatWorkConfig.getCorpId(), weChatWorkConfig.getAppSecret());
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                MAP_TYPE_REF);
        Map<String, Object> result = response.getBody();
        if (result != null && (Integer) result.get("errcode") == 0) {
            return (String) result.get("access_token");
        }
        throw new RuntimeException("Failed to get access token: " + result.get("errmsg"));
    }

    public Map<String, Object> getUserBasicInfo(String accessToken, String code) {
        String url = String.format(GET_USER_TICKET_URL, accessToken, code);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                MAP_TYPE_REF);
        Map<String, Object> result = response.getBody();
        if (result != null && (Integer) result.get("errcode") == 0) {
            return result;
        }
        throw new RuntimeException("Failed to get user basic info: " + result.get("errmsg"));
    }

    public Map<String, Object> getUserInfoByTicket(String accessToken, String userTicket) {
        String url = String.format(GET_USER_INFO_URL, accessToken);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_ticket", userTicket);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                MAP_TYPE_REF);
        Map<String, Object> result = response.getBody();
        if (result != null && (Integer) result.get("errcode") == 0) {
            return result;
        }
        throw new RuntimeException("Failed to get user info by ticket: " + result.get("errmsg"));
    }

    public Map<String, Object> getUserInfoById(String accessToken, String userId) {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s",
                accessToken, userId);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                MAP_TYPE_REF);
        Map<String, Object> result = response.getBody();
        if (result != null && (Integer) result.get("errcode") == 0) {
            return result;
        }
        throw new RuntimeException("Failed to get user info by id: " + result.get("errmsg"));
    }
}
