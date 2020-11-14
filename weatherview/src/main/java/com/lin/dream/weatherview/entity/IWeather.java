package com.lin.dream.weatherview.entity;

/**
 * <p> Title: WeatherEntity </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/28/20.
 * @date 10/28/20
 */

public interface IWeather {
    /**
     * 温度
     *
     * @return
     */
    int temperature();

    /**
     * 湿度
     *
     * @return
     */
    int humidity();

    /**
     * 空气质量
     *
     * @return
     */
    String airQuality();

    /**
     * 体感
     *
     * @return
     */
    String somatosensory();

    /**
     * 天气描述
     *
     * @return
     */
    String weatherDescription();

    /**
     * 风向
     *
     * @return
     */
    String windDirection();

    /**
     * 风力
     *
     * @return
     */
    int windLevel();

    /**
     * 时间
     *
     * @return
     */
    LinDate date();

}
