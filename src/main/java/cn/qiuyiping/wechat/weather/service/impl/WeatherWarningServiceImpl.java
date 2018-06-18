package cn.qiuyiping.wechat.weather.service.impl;

import cn.qiuyiping.wechat.weather.entity.WeatherWarning;
import cn.qiuyiping.wechat.weather.mapper.WeatherWarningMapper;
import cn.qiuyiping.wechat.weather.service.WeatherWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherWarningServiceImpl implements WeatherWarningService {

    @Autowired
    private WeatherWarningMapper weatherWarningMapper;

    @Override
    public int add(WeatherWarning warning) {
        return weatherWarningMapper.insert(warning);
    }

    @Override
    public WeatherWarning findByUrl(WeatherWarning warning) {
        return weatherWarningMapper.findByUrl(warning);
    }

    @Override
    public Map<String, WeatherWarning> listByUrls(WeatherWarning warning) {
        Map<String, WeatherWarning> map = new HashMap<>();
        List<WeatherWarning> warningList = weatherWarningMapper.listByUrls(warning);
        for(int i = 0; i < warningList.size(); i ++){
            warning = warningList.get(i);
            map.put(warning.getUrl(), warning);
        }
        return map;
    }
}
