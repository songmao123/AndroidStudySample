package com.sqsong.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 青松 on 2016/9/2.
 */
public class CirclePagerIndicator extends View {

    private Paint mPaint;
    private int mDotSpace;
    private int mCurrentPos;
    private int mNormalColor;
    private int mFocusedColor;
    private int mDotCount = 3;
    private float mIndicatorX;
    private float mFocusRadius;
    private float mNormalRadius;
    private List<PointF> mCirclePoints = new ArrayList<>();
    private Interpolator mStartInterpolator = new LinearInterpolator();

    public CirclePagerIndicator(Context context) {
        this(context, null);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CirclePagerIndicator);
        mNormalColor = ta.getColor(R.styleable.CirclePagerIndicator_normalDotColor, Color.GRAY);
        mFocusedColor = ta.getColor(R.styleable.CirclePagerIndicator_focusedDotColor, Color.RED);
        mNormalRadius = ta.getDimension(R.styleable.CirclePagerIndicator_normalDotRadius, dip2px(3));
        mFocusRadius = ta.getDimension(R.styleable.CirclePagerIndicator_focusedDotRadius, dip2px(5));
        mDotSpace = ta.getDimensionPixelOffset(R.styleable.CirclePagerIndicator_dotSpace, dip2px(8));
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = (int) (mDotCount * mFocusRadius * 2 + (mDotCount - 1) * mDotSpace);
        int measureHeight = (int) (mFocusRadius * 2);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        prepareCirclePoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircles(canvas);
        drawIndicator(canvas);
    }

    private void drawCircles(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mNormalColor);
        for (int i = 0; i < mCirclePoints.size(); i++) {
            PointF pointF = mCirclePoints.get(i);
            canvas.drawCircle(pointF.x, pointF.y, mNormalRadius, mPaint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFocusedColor);
        if (mCirclePoints.size() > 0) {
            canvas.drawCircle(mIndicatorX, getHeight() / 2, mFocusRadius, mPaint);
        }
    }

    private void prepareCirclePoints() {
        mCirclePoints.clear();
        if (mDotCount > 0) {
            int y = getHeight() / 2;
            int measureWidth = (int) (mDotCount * mNormalRadius * 2 + (mDotCount - 1) * mDotSpace);
            int centerSpacing = (int) (mNormalRadius * 2 + mDotSpace);
            int startX = (int) ((getWidth() - measureWidth) / 2 + mNormalRadius);
            for (int i = 0; i < mDotCount; i++) {
                PointF pointF = new PointF(startX, y);
                mCirclePoints.add(pointF);
                startX += centerSpacing;
            }
            mIndicatorX = mCirclePoints.get(mCurrentPos).x;
        }
    }

    public void onPageScrolled(int position, float positionOffset) {
        if (mCirclePoints.isEmpty()) {
            return;
        }

        int nextPosition = Math.min(mCirclePoints.size() - 1, position + 1);
        PointF current = mCirclePoints.get(position);
        PointF next = mCirclePoints.get(nextPosition);

        mIndicatorX = current.x + (next.x - current.x) * mStartInterpolator.getInterpolation(positionOffset);
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        mDotCount = viewPager.getAdapter().getCount();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CirclePagerIndicator.this.onPageScrolled(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

}
