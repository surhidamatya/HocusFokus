package com.leapfrog.hokusfokus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;

/**
 * Class to generate ProgressView. This class generates two progress view
 * <li>Outer circle to show decreasing time</li>
 * <li>Inner circle to show increasing timer</li>
 */
public class ProgressView extends View {


    private boolean stop = false;


    private float gapBetweenCircle = 30;
    private float centerTextSize = 20;

    private int outerColor = Color.rgb(221, 225, 241);
    private int overLapColor = Color.rgb(43, 104, 190);
    private int innerOverLapColor = Color.rgb(47, 140, 213);
    private int textColor = Color.rgb(253, 132, 105);
    private int completeCircleColor = Color.rgb(187, 222, 253);


    private Paint innerCirclePaint;
    private Paint stopTextPaint;
    private Paint circleStopPaint;
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint finishedInnerPaint;
    private Paint unfinishedInnerPaint;
    private Paint textPaint;

    private int innerBackgroundColor = Color.rgb(255, 255, 255);
    private float strokeWidth = 10;


    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();


    private RectF finishedInnerRect = new RectF();
    private RectF unfinishedInnerRect = new RectF();


    private float progress = 0.00f;
    private float max = 0.0f;

    private int cx, cy;
    private int innerRadius;

    private float elapsedTimeValue = 0.0f;

    private float coordinateX, coordinateY;


    private float textHeight;
    private Canvas canvas;

    private float textXCoordinate, textYCoordinate;


    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        EventBus.register(this);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);
        gapBetweenCircle = array.getDimension(R.styleable.ProgressView_gap_between_circle, gapBetweenCircle);
        centerTextSize = array.getDimension(R.styleable.ProgressView_center_text_size, centerTextSize);
        strokeWidth = array.getDimension(R.styleable.ProgressView_stroke_width, strokeWidth);
        centerTextSize = array.getDimension(R.styleable.ProgressView_center_text_size, centerTextSize);
        array.recycle();
        initPainters();
    }

    public void setElapsedTimeValue(float elapsedTimeValue) {
        this.elapsedTimeValue = elapsedTimeValue;
        invalidate();
    }

    private void initPainters() {

        max = (float) DateTimeUtils.focusTimeDifference(PrefUtil.getFocusHourStartTime(), PrefUtil.getFocusHourEndTime()) / 1000;
        stop = PrefUtil.getTimerState();

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(innerBackgroundColor);
        innerCirclePaint.setAntiAlias(true);

        circleStopPaint = new Paint();
        circleStopPaint.setColor(completeCircleColor);
        circleStopPaint.setAntiAlias(true);

        finishedPaint = new Paint();
        finishedPaint.setColor(outerColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(strokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(overLapColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(strokeWidth);

        finishedInnerPaint = new Paint();
        finishedInnerPaint.setColor(innerOverLapColor);
        finishedInnerPaint.setStyle(Paint.Style.STROKE);
        finishedInnerPaint.setAntiAlias(true);
        finishedInnerPaint.setStrokeWidth(strokeWidth);

        unfinishedInnerPaint = new Paint();
        unfinishedInnerPaint.setColor(outerColor);
        unfinishedInnerPaint.setStyle(Paint.Style.STROKE);
        unfinishedInnerPaint.setAntiAlias(true);
        unfinishedInnerPaint.setStrokeWidth(strokeWidth);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(centerTextSize);
        textPaint.setFakeBoldText(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setAntiAlias(true);

        stopTextPaint = new Paint();
        stopTextPaint.setColor(innerBackgroundColor);
        stopTextPaint.setTextSize(centerTextSize);
        stopTextPaint.setFakeBoldText(true);
        stopTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        stopTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingX = getPaddingLeft() + getPaddingRight();
        int paddingY = getPaddingTop() + getPaddingBottom();
        int width = getMeasuredWidth() - paddingX;
        int height = getMeasuredHeight() - paddingY;
        int size = Math.min(width, height);
    }


    public float getProgressAngle() {
        return (elapsedTimeValue * (360 / max));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.canvas = canvas;
        int size = Math.min(getWidth(), getHeight());
        int radius = size / 2 - getPaddingLeft();
        cx = getWidth() / 2;
        cy = getHeight() / 2;
        innerRadius = radius - (int) gapBetweenCircle;

        float outerRectX1 = cx - radius;
        float outerRectY1 = cy - radius;
        float outerRectX2 = cx + radius;
        float outerRectY2 = cy + radius;

        float innerRectX1 = cx - innerRadius;
        float innerRectY1 = cy - innerRadius;
        float innerRectX2 = cx + innerRadius;
        float innerRectY2 = cy + innerRadius;

        textHeight = textPaint.descent() + textPaint.ascent();

        textXCoordinate = (cx - textPaint.measureText(Constants.STOP_MESSAGE) / 2);
        textYCoordinate = (cy - textHeight / 2);


        finishedOuterRect.set(outerRectX1, outerRectY1, outerRectX2, outerRectY2);
        unfinishedOuterRect.set(outerRectX1, outerRectY1, outerRectX2, outerRectY2);

        finishedInnerRect.set(innerRectX1, innerRectY1, innerRectX2, innerRectY2);
        unfinishedInnerRect.set(innerRectX1, innerRectY1, innerRectX2, innerRectY2);


        if (isForceStop()) {
            drawTaskCompleteProgressView();
            PrefUtil.setTimerState(false);
        } else {
            drawOnProgressView();
        }

        if (DateTimeUtils.isFocusTime()) {
            if (stop) {
                drawStoppedState();
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getProgressAngle() != 360) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    coordinateX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    coordinateY = event.getY();
                    if (!stop) {
                        if (isPointLiesInsideCircle(coordinateX, coordinateY)) {
                            if (!stop) {
                                stop = true;
                                PrefUtil.setTimerState(true);
                                EventBus.post(new HokusFokusEvent.PauseProgressView(true));
                            } else {
                                stop = false;
                                EventBus.post(new HokusFokusEvent.PauseProgressView(false));
                            }
                        }
                        invalidate();
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isPointLiesInsideCircle(float x, float y) {
        return finishedInnerRect.contains(x, y) ? true : false;
    }

    private boolean forceStop = false;

    public boolean isForceStop() {
        return forceStop;
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
    }

    /**
     *Draw the progress view according to the elapsed time
     */
    private void drawOnProgressView() {
        if (!isCanvasEmpty(canvas)) {
            canvas.drawCircle(cx, cy, innerRadius - strokeWidth, innerCirclePaint);

            canvas.drawArc(finishedOuterRect, Constants.ROTATE_DEGREE_START, getProgressAngle(), false, finishedPaint);
            canvas.drawArc(unfinishedOuterRect, getProgressAngle(), Constants.ROTATE_DEGREE_FULL - getProgressAngle(), false, unfinishedPaint);

            canvas.drawArc(finishedInnerRect, Constants.ROTATE_DEGREE_START, getProgressAngle(), false, finishedInnerPaint);
            canvas.drawArc(unfinishedInnerRect, getProgressAngle(), Constants.ROTATE_DEGREE_FULL - getProgressAngle(), false, unfinishedInnerPaint);

            canvas.drawText(Constants.STOP_MESSAGE, textXCoordinate, textYCoordinate, textPaint);
        }

    }

    /**
     *draw progress view
     */
    private void drawTaskCompleteProgressView() {
        if (!isCanvasEmpty(canvas)) {
            canvas.drawArc(finishedOuterRect, Constants.ROTATE_DEGREE_START, Constants.ROTATE_DEGREE_FULL, false, finishedPaint);
            canvas.drawArc(unfinishedOuterRect, Constants.ROTATE_DEGREE_FULL, Constants.ROTATE_DEGREE_START, false, unfinishedPaint);

            canvas.drawArc(finishedInnerRect, Constants.ROTATE_DEGREE_START, Constants.ROTATE_DEGREE_FULL, false, finishedInnerPaint);
            canvas.drawArc(unfinishedInnerRect, Constants.ROTATE_DEGREE_FULL, Constants.ROTATE_DEGREE_START, false, unfinishedInnerPaint);

            canvas.drawText(Constants.COMPLETE_MESSAGE, textXCoordinate, textYCoordinate, textPaint);
        }
    }

    private void drawStoppedState() {
        canvas.drawCircle(cx, cy, innerRadius - strokeWidth, circleStopPaint);
        canvas.drawText(Constants.STOP_MESSAGE, textXCoordinate, textYCoordinate, stopTextPaint);
    }

    private boolean isCanvasEmpty(Canvas canvas) {
        return canvas == null ? true : false;
    }
}