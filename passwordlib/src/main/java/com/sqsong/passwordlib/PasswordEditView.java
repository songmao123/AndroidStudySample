package com.sqsong.passwordlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by sqsong on 17-5-30.
 */

public class PasswordEditView extends AppCompatEditText {

    private static final String TAG = PasswordEditView.class.getSimpleName();
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];

    // 最大密码位数
    private int mMaxPassNum;
    // 密码圆点alpha值数组
    private int[] alphaArray;
    // 密码边框margin距离
    private int mBorderMargin;
    // 密码圆点缩放数组
    private float[] scaleArray;
    // 边框画笔
    private Paint mBorderPaint;
    // 边框宽度
    private float mBorderWidth;
    // 是否执行焦点改变动画
    private boolean mFocusAnim;
    // 密码圆点画笔
    private Paint mPassDotPaint;
    // 边框圆角弧度
    private float mBorderRadius;
    // 当前文字长度
    private int mCurrentTextLen;
    // 密码圆点半径
    private float mPassDotRadius;
    // 背景画笔
    private Paint mBackgroundPaint;
    // 正常边框颜色
    private int mNormalBorderColor;
    // 是否inflate完成
    private boolean isFinishInflate;
    // 获取焦点时边框颜色
    private int mFocusedBorderColor;
    // 正常密码圆点颜色
    private int mNormalPassDotColor;
    // 获取焦点后的密码圆点颜色
    private int mFocusedPassDotColor;

    private PasswordEditViewCallback mCallback;

    public PasswordEditView(Context context) {
        this(context, null);
    }

    public PasswordEditView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public PasswordEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initAnimArray();
        initPaint();
        initParams();
    }

    private void initAnimArray() {
        alphaArray = new int[mMaxPassNum];
        scaleArray = new float[mMaxPassNum];

        for (int i = 0; i < alphaArray.length; i++) {
            alphaArray[i] = 255;
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditView);
        mNormalBorderColor = ta.getColor(R.styleable.PasswordEditView_normalBorderColor, Color.GRAY);
        mFocusedBorderColor = ta.getColor(R.styleable.PasswordEditView_focusedBorderColor, Color.BLACK);
        mNormalPassDotColor = ta.getColor(R.styleable.PasswordEditView_normalPassDotColor, Color.BLACK);
        mFocusedPassDotColor = ta.getColor(R.styleable.PasswordEditView_focusedPassDotColor, Color.BLACK);
        mBorderRadius = ta.getDimension(R.styleable.PasswordEditView_borderRadius, dip2px(3));
        mMaxPassNum = ta.getInt(R.styleable.PasswordEditView_maxPassNum, 6);
        mPassDotRadius = ta.getDimension(R.styleable.PasswordEditView_passDotRadius, dip2px(10));
        mFocusAnim = ta.getBoolean(R.styleable.PasswordEditView_focusAnim, false);
        ta.recycle();

        mBorderWidth = dip2px(1);
        mBorderMargin = dip2px(1);
    }

    private void initPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mNormalBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mPassDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPassDotPaint.setColor(mNormalPassDotColor);
        mPassDotPaint.setStyle(Paint.Style.FILL);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    private void initParams() {
        setCursorVisible(false); //光标不可见
        setInputType(InputType.TYPE_CLASS_NUMBER); //设置输入的是数字
        //设置输入最大长度
        setMaxLen(mMaxPassNum);
        setTextIsSelectable(false);//设置文字不可选中
    }

    private void setMaxLen(int maxLength) {
        if (maxLength >= 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            setFilters(NO_FILTERS);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.save();
        drawBorder(canvas, width, height);
        drawDot(canvas, width, height);
        canvas.restore();
    }

    /**
     * 绘制密码圆点
     *
     * @param canvas 画布
     * @param width  宽度
     * @param height 高度
     */
    private void drawDot(Canvas canvas, int width, int height) {
        float cy = height * 1.0f / 2;
        float halfGridWidth = width * 1.0f / mMaxPassNum / 2;
        float cx;
        for (int i = 0; i < mMaxPassNum; i++) {
            mPassDotPaint.setAlpha(alphaArray[i]);
            cx = width * i / mMaxPassNum + halfGridWidth;
            Log.i(TAG, "onDraw scales[" + i + "]: " + scaleArray[i]);
            canvas.drawCircle(cx, cy, mPassDotRadius * scaleArray[i], mPassDotPaint);
        }
    }

    /**
     * 绘制边框
     *
     * @param canvas 画布
     * @param width  宽度
     * @param height 高度
     */
    private void drawBorder(Canvas canvas, int width, int height) {
        // 绘制背景
        canvas.drawRect(0, 0, width, height, mBackgroundPaint);

        RectF rectF = new RectF(mBorderMargin, mBorderMargin,
                width - mBorderMargin, height - mBorderMargin);
        canvas.drawRoundRect(rectF, mBorderRadius, mBorderRadius, mBorderPaint);

        float gridWidth = ((width/* - mBorderMargin * 2*/) * 1.0f / mMaxPassNum);
        float sy = mBorderMargin + mBorderWidth * 1.0f / 2;
        float ey = height - mBorderMargin - mBorderWidth * 1.0f / 2;
        for (int i = 0; i < mMaxPassNum - 1; i++) {
            float sx = gridWidth * (i + 1);
            canvas.drawLine(sx, sy, sx, ey, mBorderPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (start != text.length()) {
            setSelection(getText().length());
        }
        if (!isFinishInflate) {
            return;
        }

        mCurrentTextLen = text.toString().length();
        final boolean isAdd = lengthAfter - lengthBefore > 0;
        startTextChangedAnim(isAdd);

        if (mCurrentTextLen == mMaxPassNum && mCallback != null) {
            mCallback.onPasswordComplete(this, getText().toString());
        }
    }

    private void startTextChangedAnim(boolean isAdd) {
        final ValueAnimator scanAnim;
        final ValueAnimator alphaAnim;
        final int index;
        if (isAdd) {
            index = mCurrentTextLen - 1;
            scanAnim = ValueAnimator.ofFloat(0F, 1F);
            alphaAnim = ValueAnimator.ofInt(0, 255);
        } else {
            index = mCurrentTextLen;
            scanAnim = ValueAnimator.ofFloat(1F, 0F);
            alphaAnim = ValueAnimator.ofInt(255, 0);
        }

        if (scaleArray.length >= mCurrentTextLen) {
            scanAnim.setDuration(750);
            scanAnim.setRepeatCount(0);
            scanAnim.setInterpolator(new OvershootInterpolator());
            scanAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float scale = (float) valueAnimator.getAnimatedValue();
                    scaleArray[index] = scale;
                    postInvalidate();
                }
            });

            alphaAnim.setDuration(750);
            alphaAnim.setRepeatCount(0);
            alphaAnim.setInterpolator(new LinearInterpolator());
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int alpha = (int) valueAnimator.getAnimatedValue();
                    alphaArray[index] = alpha;
                    postInvalidate();
                }
            });

            scanAnim.start();
            alphaAnim.start();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            setSelection(getText().length());
        }
        if (mFocusAnim) {
            startFocusChangedAnim(focused);
        } else {
            if (focused) {
                mBorderPaint.setColor(mFocusedBorderColor);
                mPassDotPaint.setColor(mFocusedPassDotColor);
            } else {
                mBorderPaint.setColor(mNormalBorderColor);
                mPassDotPaint.setColor(mNormalPassDotColor);
            }
            postInvalidate();
        }
    }

    private void startFocusChangedAnim(final boolean focused) {
        final ValueAnimator scanAnim = ValueAnimator.ofFloat(1F, 0.1F, 1F);
        scanAnim.setDuration(750);
        scanAnim.setRepeatCount(0);
        scanAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scanAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = (float) valueAnimator.getAnimatedValue();
                for (int i = 0; i < scaleArray.length; i++) {
                    if (scaleArray[i] != 0) {
                        scaleArray[i] = scale;
                    }
                }
                if (scale <= 0.15) {
                    if (focused) {
                        mBorderPaint.setColor(mFocusedBorderColor);
                        mPassDotPaint.setColor(mFocusedPassDotColor);
                    } else {
                        mBorderPaint.setColor(mNormalBorderColor);
                        mPassDotPaint.setColor(mNormalPassDotColor);
                    }
                }
                postInvalidate();
            }
        });
        scanAnim.start();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (selEnd != getText().length()) {
            setSelection(getText().length());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isFinishInflate = true;
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //关闭 copy/paste/cut 长按文字菜单，使文字不可长按选中
        //Note: 需 setTextIsSelectable(false) 才会生效
        return null;
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setPasswordEditViewCallback(PasswordEditViewCallback callback) {
        this.mCallback = callback;
    }

    public interface PasswordEditViewCallback {
        void onPasswordComplete(PasswordEditView passwordEditView, String password);
    }
}
