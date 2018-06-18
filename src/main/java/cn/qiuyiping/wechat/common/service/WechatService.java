package cn.qiuyiping.wechat.common.service;

import cn.qiuyiping.wechat.utils.SpringUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WechatService {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String secret;

    private final static String WECHAT_ACCESS_TOKEN_REDIS_KEY = "wechat.access_token";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getAccessToken() {
        String token = redisTemplate.opsForValue().get(WECHAT_ACCESS_TOKEN_REDIS_KEY);
        if(token == null) {
            Map<String, String> params = new HashMap<>();
            params.put("appId", appId);
            params.put("secret", secret);
            JSONObject data = restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={secret}", JSONObject.class, params);
            token = data.getString("access_token");
            if(token != null) {
                redisTemplate.opsForValue().set(WECHAT_ACCESS_TOKEN_REDIS_KEY, token, 3600, TimeUnit.SECONDS);
            }
        }
        return token;
    }

    public int sendModelMessage(String touser, String templateId, String url, JSONObject data) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getAccessToken();
        Map<String, Object> params = new HashMap<>();
        params.put("touser", touser);
        params.put("template_id", templateId);
        params.put("url", url);
        params.put("data", data);
        JSONObject result = restTemplate.postForObject(requestUrl, params, JSONObject.class);

        return result.getInteger("errcode");
    }
}
