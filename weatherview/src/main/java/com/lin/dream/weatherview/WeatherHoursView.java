package com.lin.dream.weatherview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.dream.weatherview.entity.IWeather;
import com.lin.dream.weatherview.listener.Convert;
import com.lin.dream.weatherview.listener.ItemClickListener;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * <p> Title: WeatherHoursView </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/31/20.
 * @date 10/31/20
 */

public class WeatherHoursView extends WeatherLayout implements Initialize<WeatherHoursView> {

    protected List<IWeather> hourWeathers;

    public WeatherHoursView(Context context) {
        this(context, null);
    }

    public WeatherHoursView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherHoursView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context, attrs);
    }

    private void initResources(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherHoursView);
        if (options == null) {
            options = new ChartView.Options();
        }
        int lineStyle = typedArray.getInt(R.styleable.WeatherHoursView_lineStyle, 1);
        options.setStyle(lineStyle == 0 ? ChartView.Style.Polyline : ChartView.Style.Curve)
                .setStrokeWidth((int) typedArray.getDimension(R.styleable.WeatherHoursView_strokeWidth, ChartView.dp2))
                .setTemperatureScale((int) typedArray.getDimension(R.styleable.WeatherHoursView_temperatureScale, ChartView.dp3))
                .setBezierFactor(typedArray.getFloat(R.styleable.WeatherHoursView_bezierFactor, 0.2f))
                .setDayColor(typedArray.getColor(R.styleable.WeatherHoursView_lineColor, ChartView.DEFAULT_DAY_COLOR))
                .setDayShadowColor(typedArray.getColor(R.styleable.WeatherHoursView_shadowColor, ChartView.DEFAULT_DAY_SHADOW_COLOR))
                .setItemWidth((int) typedArray.getDimension(R.styleable.WeatherHoursView_itemWidth, DEFAULT_ITEM_WIDTH))
                .setShadowRadius((int) typedArray.getDimension(R.styleable.WeatherHoursView_shadowRadius, ChartView.dp6))
                .setNodeRadius((int) typedArray.getDimension(R.styleable.WeatherHoursView_nodeRadius, ChartView.dp5))
                .setShadowDx((int) typedArray.getDimension(R.styleable.WeatherHoursView_shadowDx, ChartView.dp2))
                .setShadowDy((int) typedArray.getDimension(R.styleable.WeatherHoursView_shadowDy, ChartView.dp3))
                .setDashAdvance((int) typedArray.getDimension(R.styleable.WeatherHoursView_dashAdvance, ChartView.dp2))
                .setDashGap((int) typedArray.getDimension(R.styleable.WeatherHoursView_dashGap, ChartView.dp2))
                .setDashEnable(typedArray.getBoolean(R.styleable.WeatherHoursView_dashEnable, true))
                .setSolid(typedArray.getBoolean(R.styleable.WeatherHoursView_solid, true))
                .setType(ChartView.Type.Hours);
        typedArray.recycle();
    }

    @Override
    public void init() {
        if (itemLayout == -1) {
            itemLayout = R.layout.item_hours;
        }
        super.initLayout();
    }

    @Override
    public WeatherHoursView setOptions(ChartView.Options options) {
        this.options = options;
        return this;
    }

    @Override
    public WeatherHoursView setConvertWithItemLayout(Convert convert, @LayoutRes int itemLayout) {
        this.convert = convert;
        this.itemLayout = itemLayout;
        return this;
    }

    @Override
    public WeatherHoursView setListener(ItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected int getSize() {
        return hourWeathers == null ? 0 : hourWeathers.size();
    }

    @Override
    protected List<IWeather> getHoursWeather() {
        return hourWeathers;
    }

    @Override
    protected List<IWeather> getDayWeather() {
        return null;
    }

    @Override
    protected List<IWeather> getNightWeather() {
        return null;
    }

    @Override
    protected ChartView.Options getOptions() {
        if (options == null) {
            options = new ChartView.Options()
                    .setDashEnable(true)
                    .setSolid(false)
                    .setStyle(ChartView.Style.Curve)
                    .setType(ChartView.Type.Hours);
        }
        return options;
    }

    @Override
    protected int getChartViewHeight() {
        if (hourWeathers == null || hourWeathers.size() == 0) return 0;
        if (chartHeight != -1) return chartHeight;
        int highest = hourWeathers.get(0).temperature();
        int lowest = highest;
        for (int i = 1; i < hourWeathers.size(); i++) {
            lowest = Math.min(lowest, hourWeathers.get(i).temperature());
            highest = Math.max(highest, hourWeathers.get(i).temperature());
        }
        chartHeight = (highest - lowest) * TEMP_RATE + (chartOffsetY << 1);
        return chartHeight;
    }

    @SuppressLint( "DefaultLocale" )
    @Override
    protected void initWeatherInfo(View root, int index) {
        TextView tvTemperature = root.findViewById(R.id.tvTemperature);
        TextView tvWeatherDesc = root.findViewById(R.id.tvWeatherDesc);
        TextView tvWindDir = root.findViewById(R.id.tvWindDir);
        TextView tvWindLevel = root.findViewById(R.id.tvWindLevel);
        TextView tvTime = root.findViewById(R.id.tvTime);
        ImageView ivWeather = root.findViewById(R.id.ivWeather);
        tvTemperature.setText(String.format("%d°", hourWeathers.get(index).temperature()));
        tvWeatherDesc.setText(hourWeathers.get(index).weatherDescription());
        tvWindDir.setText(hourWeathers.get(index).windDirection());
        tvWindLevel.setText(String.format("%d级", hourWeathers.get(index).windLevel()));
        tvTime.setText(String.format("%2d:%2d", hourWeathers.get(index).date().hour,
                hourWeathers.get(index).date().minute));
    }

}
