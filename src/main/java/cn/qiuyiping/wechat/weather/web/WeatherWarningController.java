package cn.qiuyiping.wechat.weather.web;

import cn.qiuyiping.wechat.weather.entity.WeatherWarning;
import cn.qiuyiping.wechat.weather.service.WeatherWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather/wearning")
public class WeatherWarningController {

    @Autowired
    private WeatherWarningService weatherWarningService;

    @RequestMapping("/findByUrl")
    public String findByUrl(WeatherWarning warning) {
        warning = weatherWarningService.findByUrl(warning);
        if(warning != null) {
            return warning.getWarningContent();
        } else {
            return "参数有误";
        }
    }

}
