package com.lin.dream.weatherview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.dream.weatherview.utils.Utils;
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

public class WeatherDaysView extends WeatherLayout implements Initialize<WeatherDaysView> {

    protected List<IWeather> dayWeather;
    protected List<IWeather> nightWeather;

    public WeatherDaysView(Context context) {
        this(context, null);
    }

    public WeatherDaysView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherDaysView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context, attrs);
    }

    private void initResources(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeatherDaysView);
        if (options == null) {
            options = new ChartView.Options();
        }
        int lineStyle = typedArray.getInt(R.styleable.WeatherDaysView_lineStyle, 1);
        options.setStyle(lineStyle == 0 ? ChartView.Style.Polyline : ChartView.Style.Curve)
                .setStrokeWidth((int) typedArray.getDimension(R.styleable.WeatherDaysView_strokeWidth, ChartView.dp2))
                .setTemperatureScale((int) typedArray.getDimension(R.styleable.WeatherDaysView_temperatureScale, ChartView.dp3))
                .setBezierFactor(typedArray.getFloat(R.styleable.WeatherDaysView_bezierFactor, 0.2f))
                .setDayColor(typedArray.getColor(R.styleable.WeatherDaysView_dayColor, ChartView.DEFAULT_DAY_COLOR))
                .setNightColor(typedArray.getColor(R.styleable.WeatherDaysView_nightColor, ChartView.DEFAULT_NIGHT_COLOR))
                .setDayShadowColor(typedArray.getColor(R.styleable.WeatherDaysView_dayShadowColor, ChartView.DEFAULT_DAY_SHADOW_COLOR))
                .setNightShadowColor(typedArray.getColor(R.styleable.WeatherDaysView_nightShadowColor, ChartView.DEFAULT_NIGHT_SHADOW_COLOR))
                .setItemWidth((int) typedArray.getDimension(R.styleable.WeatherDaysView_itemWidth, DEFAULT_ITEM_WIDTH))
                .setNodeRadius((int) typedArray.getDimension(R.styleable.WeatherDaysView_nodeRadius, ChartView.dp5))
                .setShadowRadius((int) typedArray.getDimension(R.styleable.WeatherDaysView_shadowRadius, ChartView.dp6))
                .setShadowDx((int) typedArray.getDimension(R.styleable.WeatherDaysView_shadowDx, ChartView.dp2))
                .setShadowDy((int) typedArray.getDimension(R.styleable.WeatherDaysView_shadowDy, ChartView.dp3))
                .setDashAdvance((int) typedArray.getDimension(R.styleable.WeatherDaysView_dashAdvance, ChartView.dp2))
                .setDashGap((int) typedArray.getDimension(R.styleable.WeatherDaysView_dashGap, ChartView.dp2))
                .setDashEnable(typedArray.getBoolean(R.styleable.WeatherDaysView_dashEnable, true))
                .setSolid(typedArray.getBoolean(R.styleable.WeatherDaysView_solid, true))
                .setType(ChartView.Type.DayMonth);
        typedArray.recycle();
    }

    public WeatherDaysView setDayWeather(List<IWeather> dayWeather) {
        this.dayWeather = dayWeather;
        return this;
    }

    public WeatherDaysView setNightWeather(List<IWeather> nightWeather) {
        this.nightWeather = nightWeather;
        return this;
    }

    @Override
    public WeatherDaysView setOptions(ChartView.Options options) {
        this.options = options;
        return this;
    }

    @Override
    public WeatherDaysView setConvertWithItemLayout(Convert convert, @LayoutRes int itemLayout) {
        this.convert = convert;
        this.itemLayout = itemLayout;
        return this;
    }

    @Override
    public WeatherDaysView setListener(ItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void init() {
        if (this.itemLayout == -1) {
            this.itemLayout = R.layout.item_days;
        }
        super.initLayout();
    }

    @Override
    protected int getSize() {
        return dayWeather == null ? 0 : dayWeather.size();
    }

    @Override
    protected List<IWeather> getHoursWeather() {
        return null;
    }

    @Override
    protected List<IWeather> getDayWeather() {
        return dayWeather;
    }

    @Override
    protected List<IWeather> getNightWeather() {
        return nightWeather;
    }

    @Override
    protected ChartView.Options getOptions() {
        if (options == null) {
            options = new ChartView.Options()
                    .setDashEnable(true)
                    .setTemperatureScale(Utils.dp2px(3))
                    .setDayColor(ChartView.DEFAULT_DAY_COLOR)
                    .setNightColor(ChartView.DEFAULT_NIGHT_COLOR)
                    .setSolid(true)
                    .setStyle(ChartView.Style.Curve)
                    .setType(ChartView.Type.DayMonth);
        }
        return options;
    }

    @Override
    protected int getChartViewHeight() {
        if (dayWeather == null || dayWeather.size() == 0) return 0;
        if (chartHeight != -1) return chartHeight;
        int highest = dayWeather.get(0).temperature();
        int lowest = highest;
        for (int i = 1; i < dayWeather.size(); i++) {
            lowest = Math.min(lowest, dayWeather.get(i).temperature());
            highest = Math.max(highest, dayWeather.get(i).temperature());
        }
        for (int i = 1; i < nightWeather.size(); i++) {
            lowest = Math.min(lowest, nightWeather.get(i).temperature());
            highest = Math.max(highest, nightWeather.get(i).temperature());
        }
        chartHeight = (highest - lowest) * TEMP_RATE + (chartOffsetY << 1);
        return chartHeight;
    }

    @SuppressLint( "DefaultLocale" )
    @Override
    protected void initWeatherInfo(View root, int index) {
        TextView tvWeekDay = root.findViewById(R.id.tvWeekDay);
        TextView tvDate = root.findViewById(R.id.tvDate);
        ImageView ivDayWeather = root.findViewById(R.id.ivDayWeather);
        TextView tvDayWeatherDesc = root.findViewById(R.id.tvDayWeatherDesc);
        TextView tvDayTemperature = root.findViewById(R.id.tvDayTemperature);
        TextView tvNightTemperature = root.findViewById(R.id.tvNightTemperature);
        TextView tvNightWeatherDesc = root.findViewById(R.id.tvNightWeatherDesc);
        ImageView ivNightWeather = root.findViewById(R.id.ivNightWeather);
        TextView tvWindDir = root.findViewById(R.id.tvWindDir);
        TextView tvWindLevel = root.findViewById(R.id.tvWindLevel);
        TextView tvAir = root.findViewById(R.id.tvAir);
        tvWeekDay.setText(dayWeather.get(index).date().getWeekStr());
        tvDate.setText(String.format("%2d/%2d", dayWeather.get(index).date().month,
                dayWeather.get(index).date().day));
        tvDayTemperature.setText(String.format("%d°", dayWeather.get(index).temperature()));
        tvDayWeatherDesc.setText(dayWeather.get(index).weatherDescription());
        tvWindDir.setText(dayWeather.get(index).windDirection());
        tvWindLevel.setText(String.format("%d级", dayWeather.get(index).windLevel()));
        tvNightTemperature.setText(String.format("%d°", nightWeather.get(index).temperature()));
        tvNightWeatherDesc.setText(nightWeather.get(index).weatherDescription());
        tvAir.setText(dayWeather.get(index).airQuality());
    }

}
