package com.lin.dream.weatherview.listener;

import android.view.View;

/**
 * <p> Title: Convert </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/30/20.
 * @date 10/30/20
 */

public interface Convert {
    /**
     * 由调用者自己绑定视图和数据
     *
     * @param view
     * @param index
     */
    void onItemInflater(View view, int index);
}
