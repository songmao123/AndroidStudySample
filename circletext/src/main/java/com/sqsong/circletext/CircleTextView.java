package com.sqsong.circletext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 青松 on 2017/6/22.
 */

public class CircleTextView extends View {

    private String text;
    private Paint mPaint;
    private int textColor;
    private float padding;
    private float textSize;
    private int borderColor;
    private float borderWidth;
    private int backgroundColor;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
            backgroundColor = typedArray.getColor(R.styleable.CircleTextView_backgroundColor, Color.CYAN);
            textColor = typedArray.getColor(R.styleable.CircleTextView_textColor, Color.WHITE);
            textSize = typedArray.getDimension(R.styleable.CircleTextView_textSize, getResources().getDimension(R.dimen.default_text_size));
            padding = typedArray.getDimension(R.styleable.CircleTextView_padding, getResources().getDimension(R.dimen.default_padding));
            borderWidth = typedArray.getDimension(R.styleable.CircleTextView_borderWidth, 0);
            borderColor = typedArray.getColor(R.styleable.CircleTextView_borderColor, Color.RED);
            text = typedArray.getString(R.styleable.CircleTextView_text);

            if (text == null) {
                text = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(textSize);
        int textWidth = (int) mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int textHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        int size = (int) (Math.max(textWidth, textHeight) + borderWidth * 2 + padding * 2);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        float x = getWidth() / 2 - mPaint.measureText(text) / 2;
        float baseY = getHeight() / 2 - (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.drawText(text, x, baseY, mPaint);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        float radius = (getWidth()) / 2  - borderWidth;
        canvas.drawCircle(getHeight() / 2, getHeight() / 2, radius, mPaint);
    }

    private void drawBorder(Canvas canvas) {
        if (borderWidth <= 0) return;
        mPaint.setColor(borderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderWidth);
        float radius = (getWidth() - borderWidth) / 2;
        canvas.drawCircle(getHeight() / 2 , getHeight() / 2, radius, mPaint);
    }

    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            this.text =text;
            invalidate();
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
