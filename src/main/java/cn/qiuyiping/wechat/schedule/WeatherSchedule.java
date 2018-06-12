package cn.qiuyiping.wechat.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Component
public class WeatherSchedule {

    @Scheduled(cron = "0 0/10 * * * ?")
    public void weatherWarning() {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
//        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://product.weather.com.cn/alarm/grepalarm_cn.php", String.class);
        if(StringUtils.isEmpty(result)) {
            return;
        }

        result = result.replace("var alarminfo=", "").replace(";", "");
        System.out.println(result);
        JSONObject obj = JSONObject.parseObject(result);
        JSONArray data = obj.getJSONArray("data");
        for(int i = 0; i < data.size(); i ++) {
            JSONArray info = data.getJSONArray(i);
            System.out.println(i + "\t" + info.getString(0) + "\t" + info.getString(1));
        }
    }

}
