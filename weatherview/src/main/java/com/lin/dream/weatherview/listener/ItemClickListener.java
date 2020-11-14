package com.lin.dream.weatherview.listener;

import com.lin.dream.weatherview.entity.IWeather;

/**
 * <p> Title: ItemClickListener </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 11/4/20.
 * @date 11/4/20
 */

public interface ItemClickListener {

    void onItemClick(IWeather weather);

    void onItemClick(IWeather dayWeather, IWeather nightWeather);
}
