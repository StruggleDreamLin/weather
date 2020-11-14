package com.lin.dream.weatherview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.lin.dream.weatherview.utils.Utils;
import com.lin.dream.weatherview.entity.IWeather;
import com.lin.dream.weatherview.listener.Convert;
import com.lin.dream.weatherview.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

/**
 * <p> Title: WeatherHoursLayout </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/29/20.
 * @date 10/29/20
 */

public abstract class WeatherLayout extends ViewGroup implements View.OnClickListener {

    protected final int DEFAULT_ITEM_WIDTH = Utils.dp2px(75f);
    protected final int FLAG_CHART = 0x22;
    protected final int FLAG_NORMAL = 0x11;
    protected final int KEY_TYPE = 7 << 25;           //子View类型
    protected final int KEY_INDEX = 5 << 25;          //子View索引

    /**
     * 单位温度对应的dp值
     */
    protected final int TEMP_RATE = Utils.dp2px(3);

    protected int itemWidth = DEFAULT_ITEM_WIDTH;

    /**
     * 考虑节点半径以及线宽度，还有阴影偏移及半径
     */
    protected int chartOffsetY = Utils.dp2px(10);

    /**
     * 图高度
     */
    protected int chartHeight = -1;
    protected int chartTop = -1;

    protected Convert convert;
    protected @LayoutRes
    int itemLayout = -1;
    protected ChartView.Options options;

    protected GestureDetectorCompat gestureDetector;
    protected OverScroller scroller;
    protected GestureListener gestureListener;
    protected FilingRunnable filingRunnable;
    protected int scaledTouchSlop;           //滑动最小距离
    protected float scrollerFriction = 3.0f; //摩擦系数
    private int lastTouchX;
    private int screenWidth;
    protected int overX = 0;
    protected int overY = 0;

    protected ItemClickListener listener;

    public WeatherLayout(Context context) {
        this(context, null);
    }

    public WeatherLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    protected void onCreate() {
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        scroller = new OverScroller(getContext());
        scroller.setFriction(scrollerFriction);
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        screenWidth = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                Resources.getSystem().getDisplayMetrics().widthPixels : Resources.getSystem().getDisplayMetrics().heightPixels;
        filingRunnable = new FilingRunnable();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getSize() * itemWidth + getPaddingStart() + getPaddingEnd();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int height = getChildCount() == 0 ? 0 : getChildAt(0).getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int curLeft = getPaddingStart();
        int top = getPaddingTop();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            Object tag = child.getTag(KEY_TYPE);
            if (tag != null && ((int) tag == FLAG_CHART)) {
                child.layout(getPaddingStart(), getChartViewTop(), r - getPaddingEnd(), t + getChartViewTop() + getChartViewHeight());
            } else {
                int childWidth = child.getMeasuredWidth();
                child.layout(curLeft, top, curLeft + childWidth, b);
                curLeft += childWidth;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = (int) ev.getX();
                //这里让gestureDetector监听到DOWN事件，否则onScroll时会导致distance计算不正确
                gestureDetector.onTouchEvent(ev);
                return false;
            case MotionEvent.ACTION_MOVE:
                int distance = (int) Math.abs(ev.getX() - lastTouchX);
                return distance > scaledTouchSlop;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;
        Object tag = v.getTag(KEY_INDEX);
        if (tag == null) return;
        int index = (int) tag;
        if (getOptions().getType() == ChartView.Type.Hours) {
            listener.onItemClick(getHoursWeather().get(index));
        } else {
            listener.onItemClick(getDayWeather().get(index),
                    getNightWeather().get(index));
        }
    }

    /**
     * item数量
     *
     * @return
     */
    protected abstract int getSize();

    /*chart的测量工作可以放到子类中去做*/
    protected int getChartViewTop() {
        if (getChildCount() == 0) return 0;
        if (chartTop != -1) return chartTop;
        View chartHolder = findViewById(R.id.chartHolder);
        chartTop = chartHolder == null ? -1 : chartHolder.getTop();
        return chartTop;
    }

    /**
     * 获取折线图高度
     *
     * @return
     */
    protected abstract int getChartViewHeight();

    protected abstract List<IWeather> getHoursWeather();

    protected abstract List<IWeather> getDayWeather();

    protected abstract List<IWeather> getNightWeather();

    protected abstract ChartView.Options getOptions();

    @Override
    public void addView(View child) {
        filterChart(child);
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        filterChart(child);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        filterChart(child);
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        filterChart(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        filterChart(child);
        super.addView(child, index, params);
    }

    private void filterChart(View child) {
        if (child instanceof ChartView) {
            child.setTag(KEY_TYPE, FLAG_CHART);
        } else {
            child.setTag(KEY_TYPE, FLAG_NORMAL);
            child.setTag(KEY_INDEX, getChildCount());
        }
    }

    //子类初始化时调用
    protected void initLayout() {
        //提前对第一个View进行测量
        performFirstItem();
        LayoutParams itemLayoutParams;
        LayoutParams layoutParams;
        for (int i = 1; i < getSize(); i++) {
            View itemView = LayoutInflater.from(getContext()).inflate(this.itemLayout, this, false);
            itemLayoutParams = itemView.getLayoutParams();
            itemLayoutParams.width = itemWidth;
            itemView.setLayoutParams(itemLayoutParams);
            View chartHolder = itemView.findViewById(R.id.chartHolder);
            layoutParams = chartHolder.getLayoutParams();
            layoutParams.height = getChartViewHeight();
            chartHolder.setLayoutParams(layoutParams);
            if (this.convert == null) {
                initWeatherInfo(itemView, i);
            } else {
                this.convert.onItemInflater(itemView, i);
            }
            itemView.setOnClickListener(this);
            addView(itemView);
        }
        addChartView(getOptions());
    }

    /**
     * 提前对第一个子Item进行测量，以获取动态计算的宽高
     */
    private void performFirstItem() {
        View firstView = LayoutInflater.from(getContext()).inflate(itemLayout, this, false);
        if (this.convert == null) {
            initWeatherInfo(firstView, 0);
        } else {
            this.convert.onItemInflater(firstView, 0);
        }
        //第一个Item提前测量 不进行任何限制
        int specUnSpecified = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        firstView.measure(specUnSpecified, specUnSpecified);
        LayoutParams itemLayoutParams = firstView.getLayoutParams();
        View firstHolder = firstView.findViewById(R.id.chartHolder);
        if (itemLayoutParams.width == LayoutParams.MATCH_PARENT) {
            itemLayoutParams.width = itemWidth;
            firstView.setLayoutParams(itemLayoutParams);
        } else if (itemLayoutParams.width == LayoutParams.WRAP_CONTENT) {
            itemWidth = firstView.getMeasuredWidth();
        } else {
            //固定尺寸
            itemWidth = itemLayoutParams.width;
        }
        LayoutParams layoutParams = firstHolder.getLayoutParams();
        //图高度替换占位符高度
        layoutParams.height = getChartViewHeight();
        firstHolder.setLayoutParams(layoutParams);
        firstView.setOnClickListener(this);
        addView(firstView);
    }

    /**
     * 添加折线图，可自己进行扩展
     */
    protected void addChartView(ChartView.Options options) {
        ChartView chartView = new ChartView(getContext());
        addView(chartView, LayoutParams.MATCH_PARENT, getChartViewHeight());
        //TODO: init chart params
        List<Integer> dayTemps = new ArrayList<>();
        List<Integer> nightTemps = new ArrayList<>();
        if (getOptions().getType() == ChartView.Type.Hours) {
            for (int i = 0; i < getSize(); i++) {
                dayTemps.add(getHoursWeather().get(i).temperature());
                if (options.isDashEnable() && options.getCurrentIndex() == 0) {
                    if (getHoursWeather().get(i).date().isCurrentHour()) {
                        options.setCurrentIndex(i);
                    }
                }
            }
        } else {
            for (int i = 0; i < getSize(); i++) {
                dayTemps.add(getDayWeather().get(i).temperature());
                nightTemps.add(getNightWeather().get(i).temperature());
                if (options.isDashEnable() && options.getCurrentIndex() == 0) {
                    if (getDayWeather().get(i).date().isToday()) {
                        options.setCurrentIndex(i);
                    }
                }
            }
        }
        chartView.initParams(dayTemps, nightTemps, options);
    }

    /**
     * 绑定数据
     *
     * @param root
     * @param index
     */
    protected abstract void initWeatherInfo(View root, int index);

    /**
     * 处理手势 滑动和快滑
     */
    class GestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int scrollX = (int) distanceX;
            int scrollToX = getScrollX() + scrollX;
            if (scrollToX < 0) {
                scrollX = -getScrollX();
            } else if (scrollToX > getWidth() - screenWidth) {
                scrollX = getWidth() - screenWidth - getScrollX();
            }
            scrollBy(scrollX, 0);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //TODO:fling
            int space = (getWidth() - screenWidth) >> 1;
            scroller.fling(0, 0, (int) velocityX, (int) velocityY,
                    -space, space, 0, 0);
            postOnAnimation(filingRunnable);
            return false;
        }
    }

    class FilingRunnable implements Runnable {
        @Override
        public void run() {
            //计算偏移结果,返回是否偏移结束
            if (scroller.computeScrollOffset()) {
                int scrollToX = getScrollX() - scroller.getCurrX();
                //修正偏移
                scrollToX = Math.max(scrollToX, 0);
                scrollToX = Math.min(scrollToX, getWidth() - screenWidth);
                scrollTo(scrollToX, 0);
                postOnAnimation(this);
            }
        }
    }
}
