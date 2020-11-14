package com.lin.dream.weatherview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.lin.dream.weatherview.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * <p> Title: ChartView </p>
 * <p> Description: </p>
 *
 * @author dreamlin
 * @version V1.0.0
 * Created by dreamlin on 10/29/20.
 * @date 10/29/20
 */

public class ChartView extends View {

    static final int dp1 = Utils.dp2px(1);
    static final int dp2 = Utils.dp2px(2);
    static final int dp3 = Utils.dp2px(3);
    static final int dp5 = Utils.dp2px(5);
    static final int dp6 = Utils.dp2px(6);
    static final int dp10 = Utils.dp2px(10);

    static final int DEFAULT_NIGHT_COLOR = Color.parseColor("#FF2D97FB");
    static final int DEFAULT_DAY_COLOR = Color.parseColor("#FFFF6200");
    static final int DEFAULT_NIGHT_SHADOW_COLOR = Color.parseColor("#662D97FB");
    static final int DEFAULT_DAY_SHADOW_COLOR = Color.parseColor("#66FF6200");

    private Paint dayPaint;
    private Paint dayNodePaint;
    private Paint nightPaint;
    private Paint nightNodePaint;
    private Path dayPath;
    private Path dayNodePath;
    private Path nightPath;
    private Path nightNodePath;
    private Path dayDashPath;
    private Path nightDashPath;
    private PathEffect dashEffect;

    /***colors***/
    private @ColorInt
    int dayColor = DEFAULT_DAY_COLOR;
    private @ColorInt
    int dayShadowColor = DEFAULT_DAY_SHADOW_COLOR;
    private @ColorInt
    int nightColor = DEFAULT_NIGHT_COLOR;
    private @ColorInt
    int nightShadowColor = DEFAULT_NIGHT_SHADOW_COLOR;

    /***width***/
    private int shadowRadius = dp6;
    private int shadowDx = dp2;
    private int shadowDy = dp3;
    private int nodeRadius = dp5;               //节点半径
    private int strokeWidth = dp2;
    private int itemWidth = Utils.dp2px(75f);   //点间距
    private int highestTemperature = 0;         //最高温度
    private int offsetPadding = Utils.dp2px(10);//偏移
    private int temperatureScale = dp3;         //一度对应几dp
    private int dashAdvance = dp2;              //虚线长度
    private int dashGap = dp2;                  //虚线间隔

    private Style style = Style.Polyline;       //线样式
    private Type type = Type.Hours;
    private int currentIndex = 0;
    private float bezierFactor = 0.2f;          //贝塞尔曲线因数

    /***boolean***/
    private boolean isSolid = false;            //节点是否实心圆
    private boolean dashEnable = false;         //是否使用虚线

    private List<Integer> dayTemperatures = new ArrayList<>();
    private List<Integer> nightTemperatures = new ArrayList<>();

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayNodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayPath = new Path();
        dayNodePath = new Path();
        dayDashPath = new Path();
        nightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nightNodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nightPath = new Path();
        nightNodePath = new Path();
        nightDashPath = new Path();
    }

    private void initDrawStyle() {
        dayPaint.setStrokeWidth(strokeWidth);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(dayColor);
        dayPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, dayShadowColor);
        dayPaint.setStrokeJoin(Paint.Join.ROUND);
        dayNodePaint.setStrokeWidth(strokeWidth);
        dayNodePaint.setStyle(isSolid ? Paint.Style.FILL : Paint.Style.STROKE);
        dayNodePaint.setColor(dayColor);
        dayNodePaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, dayShadowColor);
        dayNodePaint.setStrokeJoin(Paint.Join.ROUND);

        nightPaint.setStrokeWidth(strokeWidth);
        nightPaint.setStyle(Paint.Style.STROKE);
        nightPaint.setColor(nightColor);
        nightPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, nightShadowColor);
        nightPaint.setStrokeJoin(Paint.Join.ROUND);
        nightNodePaint.setStrokeWidth(strokeWidth);
        nightNodePaint.setStyle(isSolid ? Paint.Style.FILL : Paint.Style.STROKE);
        nightNodePaint.setColor(nightColor);
        nightNodePaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, nightShadowColor);
        nightNodePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(itemWidth * dayTemperatures.size(), getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePath(w, h);
    }

    private void calculatePath(int width, int height) {
        dayPath.reset();
        dayNodePath.reset();
        nightPath.reset();
        nightNodePath.reset();
        if (dashEnable) {
            for (int i = 0; i <= currentIndex; i++) {
                fillPath(dayDashPath, nightDashPath, i);
            }
            //修复实线偏移至虚线尾部
            int dayOffsetX = currentIndex * itemWidth + (itemWidth >> 1) + (isSolid ? 0 : nodeRadius);
            int dayOffsetY = (highestTemperature - dayTemperatures.get(currentIndex)) * temperatureScale + offsetPadding;
            dayPath.moveTo(dayOffsetX, dayOffsetY);
            if (needDrawNight()) {
                int nightOffsetY = (highestTemperature - nightTemperatures.get(currentIndex)) * temperatureScale + offsetPadding;
                nightPath.moveTo(dayOffsetX, nightOffsetY);
            }
        }
        for (int i = dashEnable ? currentIndex + 1 : 0; i < dayTemperatures.size(); i++) {
            fillPath(dayPath, nightPath, i);
        }
    }

    private void fillPath(Path pathDay, Path pathNight, int i) {
        int dayX = 0;
        int dayY = 0;
        int nightY = 0;
        dayX = itemWidth * i + (itemWidth >> 1);
        dayY = (highestTemperature - dayTemperatures.get(i)) * temperatureScale + offsetPadding;
        if (needDrawNight() && nightTemperatures.size() > i) {
            nightY = (highestTemperature - nightTemperatures.get(i)) * temperatureScale + offsetPadding;
        }
        if (i == 0) {
            int moveToX = isSolid ? dayX : dayX + nodeRadius;
            pathDay.moveTo(moveToX, dayY);
            if (needDrawNight()) {
                pathNight.moveTo(moveToX, nightY);
            }
        } else {
            //折线
            if (style == Style.Polyline) {
                pathDay.lineTo(isSolid ? dayX : dayX - nodeRadius, dayY);
                if (needDrawNight()) {
                    pathNight.lineTo(isSolid ? dayX : dayX - nodeRadius, nightY);
                    if (!isSolid)
                        pathNight.rMoveTo(nodeRadius << 1, 0);
                }
            } else {
                //曲线 计算贝塞尔曲线
                curveCubic(pathDay, dayTemperatures, i);
                if (needDrawNight()) {
                    curveCubic(pathNight, nightTemperatures, i);
                }
            }
            //如果不是实心节点，流出空心节点位置
            if (!isSolid) {
                pathDay.rMoveTo(nodeRadius << 1, 0);
                if (needDrawNight()) {
                    pathNight.rMoveTo(nodeRadius << 1, 0);
                }
            }
        }
        dayNodePath.addCircle(dayX, dayY, nodeRadius, Path.Direction.CCW);
        if (needDrawNight()) {
            nightNodePath.addCircle(dayX, nightY, nodeRadius, Path.Direction.CCW);
        }
    }

    PointF prePrevious, previous, current, next;

    {
        prePrevious = new PointF();
        previous = new PointF();
        current = new PointF();
        next = new PointF();
    }

    private void curveCubic(Path path, List<Integer> temperatures, int index) {
        //全部重新计算
        current.set(itemWidth * index + (itemWidth >> 1),
                (highestTemperature - temperatures.get(index)) * temperatureScale + offsetPadding);
        if (index > 0) {
            previous.set(current.x - itemWidth,
                    (highestTemperature - temperatures.get(index - 1)) * temperatureScale + offsetPadding);
        } else {
            previous.set(current.x, current.y);
        }
        if (index > 1) {
            prePrevious.set(previous.x - itemWidth,
                    (highestTemperature - temperatures.get(index - 2)) * temperatureScale + offsetPadding);
        } else {
            prePrevious.set(previous.x, previous.y);
        }
        if (index < temperatures.size() - 1) {
            next.set(current.x + itemWidth,
                    (highestTemperature - temperatures.get(index + 1)) * temperatureScale + offsetPadding);
        } else {
            next.set(current.x, current.y);
        }
        float firstControlX = previous.x + (current.x - prePrevious.x) * bezierFactor;
        float firstControlY = previous.y + (current.y - prePrevious.y) * bezierFactor;
        float secondControlX = current.x - (next.x - previous.x) * bezierFactor;
        float secondControlY = current.y - (next.y - previous.y) * bezierFactor;
        path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, isSolid ? current.x : current.x - nodeRadius, current.y);
    }

    private boolean needDrawNight() {
        return type == Type.DayMonth && nightTemperatures != null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!dayPath.isEmpty()) {
            dayPaint.setPathEffect(null);
            canvas.drawPath(dayPath, dayPaint);
            canvas.drawPath(dayNodePath, dayNodePaint);
        }
        if (!dayDashPath.isEmpty() && dashEnable) {
            dayPaint.setPathEffect(dashEffect);
            canvas.drawPath(dayDashPath, dayPaint);
        }
        if (!nightPath.isEmpty()) {
            nightPaint.setPathEffect(null);
            canvas.drawPath(nightPath, nightPaint);
            canvas.drawPath(nightNodePath, nightNodePaint);
        }
        if (!nightDashPath.isEmpty() && dashEnable) {
            nightPaint.setPathEffect(dashEffect);
            canvas.drawPath(nightDashPath, nightPaint);
        }
    }

    /**
     * 获取最高气温
     */
    protected void getBoundaryTemperature() {
        if (dayTemperatures != null) {
            highestTemperature = dayTemperatures.get(0);
            for (int i = 1; i < dayTemperatures.size(); i++) {
                highestTemperature = Math.max(highestTemperature, dayTemperatures.get(i));
            }
        }
        if (nightTemperatures != null) {
            for (int i = 0; i < nightTemperatures.size(); i++) {
                highestTemperature = Math.max(highestTemperature, nightTemperatures.get(i));
            }
        }
    }

    public void initParams(List<Integer> dayTemperatures, List<Integer> nightTemperatures, Options options) {
        this.dayTemperatures = dayTemperatures;
        this.nightTemperatures = nightTemperatures;
        this.type = options.type;
        this.style = options.style;
        this.itemWidth = options.itemWidth;
        this.bezierFactor = options.bezierFactor;
        this.currentIndex = options.currentIndex;
        this.dayColor = options.dayColor;
        this.dayShadowColor = options.dayShadowColor;
        this.nightColor = options.nightColor;
        this.nightShadowColor = options.nightShadowColor;
        this.nodeRadius = options.nodeRadius;
        this.strokeWidth = options.strokeWidth;
        this.temperatureScale = options.temperatureScale;
        this.dashAdvance = options.dashAdvance;
        this.dashGap = options.dashGap;
        this.dashEnable = options.dashEnable;
        this.isSolid = options.isSolid;
        this.shadowRadius = options.shadowRadius;
        this.shadowDx = options.shadowDx;
        this.shadowDy = options.shadowDy;
        initDrawStyle();
        initDash();
        getBoundaryTemperature();
        invalidate();
    }

    private void initDash() {
        if (dashEnable) {
            dashEffect = new DashPathEffect(new float[]{dashAdvance, dashGap}, 0);
        }
    }

    public static class Options {

        private Style style = Style.Polyline;
        private Type type = Type.Hours;
        private int itemWidth = Utils.dp2px(75f);
        private float bezierFactor = 0.2f;          //贝塞尔曲线因数
        private @ColorInt
        int dayColor = DEFAULT_DAY_COLOR;
        private @ColorInt
        int dayShadowColor = DEFAULT_DAY_SHADOW_COLOR;
        private @ColorInt
        int nightColor = DEFAULT_NIGHT_COLOR;
        private @ColorInt
        int nightShadowColor = DEFAULT_NIGHT_SHADOW_COLOR;
        private int shadowRadius = dp6;
        private int shadowDx = dp2;
        private int shadowDy = dp3;
        private int nodeRadius = dp5;               //节点半径
        private int strokeWidth = dp2;
        private int temperatureScale = dp3;         //一度对应几dp
        private int dashAdvance = dp2;              //虚线长度
        private int dashGap = dp2;                  //虚线间隔
        private int currentIndex = 0;               //对应当天或当前时间的温度索引

        /***boolean***/
        private boolean isSolid = true;            //节点是否实心圆
        private boolean dashEnable = true;         //是否使用虚线

        public Options setStyle(Style style) {
            this.style = style;
            return this;
        }

        public Options setType(Type type) {
            this.type = type;
            return this;
        }

        public Options setItemWidth(int itemWidth) {
            this.itemWidth = itemWidth;
            return this;
        }

        public Options setBezierFactor(float bezierFactor) {
            this.bezierFactor = bezierFactor;
            return this;
        }

        public Options setDayColor(int dayColor) {
            this.dayColor = dayColor;
            return this;
        }

        public Options setDayShadowColor(int dayShadowColor) {
            this.dayShadowColor = dayShadowColor;
            return this;
        }

        public Options setNightColor(int nightColor) {
            this.nightColor = nightColor;
            return this;
        }

        public Options setNightShadowColor(int nightShadowColor) {
            this.nightShadowColor = nightShadowColor;
            return this;
        }

        public Options setNodeRadius(int nodeRadius) {
            this.nodeRadius = nodeRadius;
            return this;
        }

        public Options setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Options setTemperatureScale(int temperatureScale) {
            this.temperatureScale = temperatureScale;
            return this;
        }

        public Options setDashAdvance(int dashAdvance) {
            this.dashAdvance = dashAdvance;
            return this;
        }

        public Options setDashGap(int dashGap) {
            this.dashGap = dashGap;
            return this;
        }

        public Options setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
            return this;
        }

        public Options setSolid(boolean solid) {
            isSolid = solid;
            return this;
        }

        public Options setDashEnable(boolean dashEnable) {
            this.dashEnable = dashEnable;
            return this;
        }

        public Options setShadowRadius(int shadowRadius) {
            this.shadowRadius = shadowRadius;
            return this;
        }

        public Options setShadowDx(int shadowDx) {
            this.shadowDx = shadowDx;
            return this;
        }

        public Options setShadowDy(int shadowDy) {
            this.shadowDy = shadowDy;
            return this;
        }

        public Style getStyle() {
            return style;
        }

        public Type getType() {
            return type;
        }

        public boolean isDashEnable() {
            return dashEnable;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }
    }

    public enum Style {
        Polyline,
        Curve
    }

    public enum Type {
        Hours,
        DayMonth
    }
}
