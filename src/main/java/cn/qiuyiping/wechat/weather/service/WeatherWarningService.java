package cn.qiuyiping.wechat.weather.service;

import cn.qiuyiping.wechat.weather.entity.WeatherWarning;

import java.util.Map;

public interface WeatherWarningService {

    int add(WeatherWarning warning);

    WeatherWarning findByUrl(WeatherWarning warning);

    Map<String,WeatherWarning> listByUrls(WeatherWarning warning);
}
