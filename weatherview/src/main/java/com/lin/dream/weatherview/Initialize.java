package com.lin.dream.weatherview;

import com.lin.dream.weatherview.listener.Convert;
import com.lin.dream.weatherview.listener.ItemClickListener;

import androidx.annotation.LayoutRes;

/**
 * <p> Title: Init </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/31/20.
 * @date 10/31/20
 */

public interface Initialize<T> {

    void init();

    public T setOptions(ChartView.Options options);

    public T setConvertWithItemLayout(Convert convert, @LayoutRes int itemLayout);

    public T setListener(ItemClickListener listener);
}
