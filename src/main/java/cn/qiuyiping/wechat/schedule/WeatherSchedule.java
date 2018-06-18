package cn.qiuyiping.wechat.schedule;

import cn.qiuyiping.wechat.common.service.WechatService;
import cn.qiuyiping.wechat.weather.entity.WeatherWarning;
import cn.qiuyiping.wechat.weather.service.WeatherWarningService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WeatherSchedule {

    private Logger logger = LoggerFactory.getLogger(WeatherSchedule.class);

    @Value("${wechat.weather-model-id}")
    private String weatherModelId;

    @Value("${wechat.touser}")
    private String touser;

    @Autowired
    private WeatherWarningService weatherWarningService;

    @Autowired
    private WechatService wechatService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void weatherWarning() {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
        String result = restTemplate.getForObject("http://product.weather.com.cn/alarm/grepalarm_cn.php", String.class);
        if(StringUtils.isEmpty(result)) {
            return;
        }

        result = result.replace("var alarminfo=", "").replace(";", "");
        JSONObject obj = JSONObject.parseObject(result);
        JSONArray data = obj.getJSONArray("data");
        if(data == null || data.size() == 0) {
            return;
        }
        List<WeatherWarning> warningList = new ArrayList<>();
        for(int i = 0; i < data.size(); i ++) {
            JSONArray info = data.getJSONArray(i);
            WeatherWarning warning = new WeatherWarning();
            warning.setArea(info.getString(0));
            warning.setUrl(info.getString(1));
            warningList.add(warning);
        }
        Map<String, WeatherWarning> warningMap = new ConcurrentReferenceHashMap<>();
        String[] urlArray = new String[data.size()];
        for(int i = 0; i < warningList.size(); i ++) {
            WeatherWarning warning = warningList.get(i);
            warningMap.put(warning.getUrl(), warning);
            urlArray[i] = warning.getUrl();
        }
        WeatherWarning warning = new WeatherWarning();
        warning.setUrlArray(urlArray);
        Map<String, WeatherWarning> existWarningMap = weatherWarningService.listByUrls(warning);

        Iterator<Map.Entry<String, WeatherWarning>> it = warningMap.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<String, WeatherWarning> entry = it.next();
            if(existWarningMap.containsKey(entry.getKey())) {
                warningMap.remove(entry.getKey());
            }
        }

        it = warningMap.entrySet().iterator();
        while(it.hasNext()) {
            warning = it.next().getValue();
            String[] info = warning.getUrl().split("-");
            warning.setAreaCode(info[0]);
            try {
                warning.setWarningTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(info[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                result = restTemplate.getForObject("http://product.weather.com.cn/alarm/webdata/" + warning.getUrl(), String.class);
                result = result.replace("var alarminfo=", "").replace(";", "");
                obj = JSONObject.parseObject(result);
                warning.setWarningContent(obj.getString("ISSUECONTENT"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            warning.setWarningType(info[2].substring(0, 2));
            warning.setWarningLevel(info[2].substring(2, 4));
            weatherWarningService.add(warning);
            JSONObject params = new JSONObject();
            JSONObject area = new JSONObject();
            area.put("value", warning.getArea());
            params.put("area", area);
            JSONObject time = new JSONObject();
            time.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(warning.getWarningTime()));
            params.put("time", time);
            JSONObject content = new JSONObject();
            content.put("value", warning.getWarningContent());
            params.put("content", content);
            wechatService.sendModelMessage(touser, weatherModelId, "http://www.weather.com.cn/alarm/newalarmcontent.shtml?file=" + warning.getUrl(), params);
        }

    }

}
