package demo.test.com.pieview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;


/**
 * author：JianFeng
 * date：2017/6/29 17:58
 * description：
 */
public class PieView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Paint paint;
    private float animatedValue;
    private RectF rectF;
    private float paintWidth;
    private float mWidth;
    private float mHeight;
    private Context context;
    private float outerDiameter;
    private float x;
    private float y;
    private float dia; //圆环内径与外径之差,即着色部分
    private float left, top; //与边界的距离
    private Paint textPaint;
    private Paint legedPaint;
    private float defMargin;
    private Rect rect;
    private float textH;
    private float defOffset;
    private float maxLength;
    private Paint circlePaint;
    private String innerCircleColor = "#FFFFFF";
    private String innerCirOutcleColor = "#55FFFFFF";
    private Paint circleOutPaint;
    private List<PieViewBean> dataList;
    private float defExternal;
    private float offestX;

    public PieView(Context context) {
        this(context, null);
        initView(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        //饼状图画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //内部填充圆的画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(getColor(innerCircleColor));
        circleOutPaint = new Paint();
        circleOutPaint.setAntiAlias(true);
        circleOutPaint.setStyle(Paint.Style.FILL);
        circleOutPaint.setColor(getColor(innerCirOutcleColor));
        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        //图例画笔
        legedPaint = new Paint();
        legedPaint.setAntiAlias(true);
        legedPaint.setStyle(Paint.Style.FILL);
        //创建扇形扫过的外围轮廓的矩形
        rectF = new RectF();
        rect = new Rect();
        //开启动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.addUpdateListener(this);
        valueAnimator.setDuration(3000);
        valueAnimator.start();

    }


    public void setDatas(List<PieViewBean> dataList) {
        this.dataList = dataList;
        invalidate();
    }

    /***
     * 圆环着色部分的宽度,单位为dp
     * 这个宽度的一半刚好穿过矩形内切线
     * @param dia
     */
    public void setAnnulusDia(float dia) {
        //画笔宽度的临界值,不能超过屏幕
        if (dia >= outerDiameter / 3) dia = outerDiameter / 3;
        paintWidth = dip2px(context, dia);
    }


    /***
     *设置外径大小
     * @param diameter
     */
    public void setOuterDiameter(float diameter) {
        if (diameter > Math.min(mWidth, mHeight)) diameter = Math.min(mWidth, mHeight);
        this.outerDiameter = diameter;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            initEdge();
        }
    }

    private void initEdge() {
        x = mWidth / 2;
        y = mHeight / 2;
        //外径,默认取宽或者高小的那个
        outerDiameter = 0;
        if (x > y) {
            outerDiameter = y;
        } else {
            outerDiameter = x;
        }
        paintWidth = dip2px(context, 30f);//画笔宽度
        textPaint.setTextSize(dip2px(context, 8f));
        legedPaint.setStrokeWidth(dip2px(context, 8f));//图例的宽度
        defMargin = dip2px(context, 20f);//默认距离边界的距离
        defOffset = dip2px(context, 0.05f);//扇形块之间的间隙,负数为间隙,正数为相交
        maxLength = getTextW(textPaint, "饼状图例", rect);
        textH = getTextH(textPaint, "饼状图", rect);
        defExternal = outerDiameter - defMargin; //默认的外边径
        offestX = defMargin * 1.5f;//x轴上偏移的距离
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF.set(x - defExternal , y - defExternal, x + defExternal, y + defExternal);
        paint.setStrokeWidth(paintWidth);
        float startAngle = 0.0f;

        for (int i = 0; i < dataList.size(); i++) {
            paint.setColor(getColor(dataList.get(i).getLegendColor()));
            //画饼状图,计算每一个比例需要扫过的面积
            float sweepAngle = 3.6f * (String2float(dataList.get(i).getRate()));
            if (Math.min(sweepAngle, animatedValue - startAngle) >= 0) {
                //下一个扇面的起始角度等于上一个扇面扫过的结束角度
                canvas.drawArc(rectF, startAngle, Math.min(sweepAngle - defOffset, animatedValue - startAngle), true, paint);
            }
            startAngle = startAngle + sweepAngle;
            //获取最长的图例文字的长度
            float textW = getTextW(textPaint, dataList.get(i).getLegendName(), rect);
            if (maxLength < textW) {
                maxLength = textW;
            }
        }
        for (int i = 0; i < dataList.size(); i++) {
            legedPaint.setColor(getColor(dataList.get(i).getLegendColor()));
            float startLineX = mWidth - maxLength - defMargin;
            float startLineY = defMargin * (i + 1);
            float stopLineX = startLineX+dip2px(context, 8f);
            float stopLineY = defMargin * (i + 1);
            float textX = stopLineX+dip2px(context, 2f);
            float textY = defMargin * (i + 1) + (textH / 3);
            canvas.drawLine(startLineX, startLineY, stopLineX, stopLineY, legedPaint);
            canvas.drawText(dataList.get(i).getLegendName(), textX, textY, textPaint);
        }
        //画内部白色的圆和阴影的圆
        canvas.drawCircle(x , y, outerDiameter / 2, circlePaint);
        canvas.drawCircle(x , y, outerDiameter / 2 + defMargin / 2, circleOutPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measuredHeight(heightMeasureSpec));
    }

    /***
     * 测量宽,默认为200
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 测量高,默认为200
     *
     * @param heightMeasureSpec
     */
    private int measuredHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        animatedValue = (float) animation.getAnimatedValue();
        invalidate();
    }


    /***
     * 字符串转float
     *
     * @param str
     * @return
     */
    public float String2float(String str) {
        if (TextUtils.isEmpty(str) || "0".equals(str)) {
            return 0;
        }
        return Float.valueOf(str);
    }

    public int getColor(String color) {
        if (TextUtils.isEmpty(color)) {
            return Color.parseColor("#FF0000");
        }
        try {
            return Color.parseColor(color);
        } catch (Exception e) {

        }
        return Color.parseColor("#FF0000");
    }

    /***
     * 获取文字的宽度
     *
     * @param paint
     * @param text
     * @return
     */
    public static float getTextW(@NonNull Paint paint, String text, @NonNull Rect rect) {
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    /***
     * 获取文字的高度
     *
     * @param paint
     * @param text
     * @return
     */
    public static float getTextH(@NonNull Paint paint, String text, @NonNull Rect rect) {
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    //数据
    static class PieViewBean {
        private String legendName;
        private String legendColor;
        private String rate;

        public void setLegendName(String legendName) {
            this.legendName = legendName;
        }

        public void setLegendColor(String legendColor) {
            this.legendColor = legendColor;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getLegendName() {
            return legendName;
        }

        public String getLegendColor() {
            return legendColor;
        }

        public String getRate() {
            return rate;
        }
    }
}
