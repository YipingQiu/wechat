package cn.qiuyiping.wechat.weather.mapper;

import cn.qiuyiping.wechat.weather.entity.WeatherWarning;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WeatherWarningMapper {

    int insert(WeatherWarning warning);

    WeatherWarning findByUrl(WeatherWarning warning);

    List<WeatherWarning> listByUrls(WeatherWarning warning);
}
