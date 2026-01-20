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
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Component
public class WeChatWorkUtil {

    @Autowired
    private WeChatWorkConfig weChatWorkConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GET_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    private static final String GET_USER_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s";
    private static final String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserdetail?access_token=%s";
    private static final String GET_JSAPI_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=%s";

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

    public String getJsApiTicket() {
        String accessToken = getAccessToken();
        String url = String.format(GET_JSAPI_TICKET_URL, accessToken);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                MAP_TYPE_REF);
        Map<String, Object> result = response.getBody();
        if (result != null && (Integer) result.get("errcode") == 0) {
            return (String) result.get("ticket");
        }
        throw new RuntimeException("Failed to get jsapi ticket: " + result.get("errmsg"));
    }

    public Map<String, String> getJsApiSignature(String url) {
        String jsApiTicket = getJsApiTicket();
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;

        String signature = generateSignature(jsApiTicket, nonceStr, timestamp, url);

        Map<String, String> signatureMap = new HashMap<>();
        signatureMap.put("timestamp", String.valueOf(timestamp));
        signatureMap.put("nonceStr", nonceStr);
        signatureMap.put("signature", signature);

        return signatureMap;
    }

    private String generateSignature(String jsApiTicket, String nonceStr, long timestamp, String url) {
        String stringToSign = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s",
                jsApiTicket, nonceStr, timestamp, url);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(stringToSign.getBytes("UTF-8"));

            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
