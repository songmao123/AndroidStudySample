package com.sqsong.recyclerlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.sqsong.recyclerlib.util.ColorUtil;
import com.sqsong.recyclerlib.util.DensityUtil;

/**
 * Created by 青松 on 2017/6/23.
 */

public class IndexBar extends ViewGroup {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private int mPosition;
    private float mRadius;
    private float mCenterY;
    private int mChildWidth;
    private String mIndexStr;
    private boolean showIndex = false;

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mRadius = DensityUtil.dip2px(35);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureSize(widthMeasureSpec, mWidth);
        mHeight = measureSize(heightMeasureSpec, mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureSize(int measureSpec, int defaultValue) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
            defaultValue = size;
        }
        return defaultValue;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0) return;
        View childAt = getChildAt(0);
        mChildWidth = childAt.getMeasuredWidth();
        childAt.layout(mWidth - mChildWidth, 0, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showIndex) {
            ColorUtil.setPaintColor(mPaint, mPosition);
            canvas.drawCircle((mWidth - mChildWidth) / 2, mCenterY, mRadius, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(DensityUtil.dip2px(30));
            canvas.drawText(mIndexStr, (mWidth - mChildWidth - mPaint.measureText(mIndexStr)) / 2,
                    mCenterY - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
        }
    }

    public void setDrawData(String indexStr, float y, int position) {
        mIndexStr = indexStr;
        mCenterY = y;
        mPosition = position;
        showIndex = true;
        invalidate();
    }

    public void hideIndexCircle() {
        this.showIndex = false;
        invalidate();
    }
}
