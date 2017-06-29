package com.sqsong.passwordlib;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PasswordPayLoadingView extends View {

    private Paint mPaint;
    private int progressColor;    //进度颜色
    private StatusEnum mStatus;     //状态
    private int loadSuccessColor;    //成功的颜色
    private int loadFailureColor;   //失败的颜色
    private float progressWidth;    //进度宽度
    private float progressRadius;   //圆环半径
    private PathMeasure mPathMeasure;


    private int curAngle = 0;
    private int minAngle = -90;
    private int startAngle = -90;
    private int sweepAngle = 120;

    //追踪Path的坐标
    //画圆的Path
    private Path mPathCircle;
    //截取PathMeasure中的path
    private Path mPathCircleDst;
    private Path successPath;
    private Path failurePathLeft;
    private Path failurePathRight;

    private float circleValue;
    private float successValue;
    private float failValueRight;
    private float failValueLeft;
    private ValueAnimator circleAnimator;
    private LoadingCompleteListener mLoadingListener;

    public enum StatusEnum {
        Loading,
        LoadSuccess,
        LoadFailure
    }

    public void setLoadingCompleteListener(LoadingCompleteListener l) {
        this.mLoadingListener = l;
    }

    public interface LoadingCompleteListener {
        void loadingComplete(View view, boolean success);
    }

    public PasswordPayLoadingView(Context context) {
        this(context, null);
    }

    public PasswordPayLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordPayLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordPayLoadingView, defStyleAttr, 0);
        progressColor = array.getColor(R.styleable.PasswordPayLoadingView_progress_color, Color.RED);
        loadSuccessColor = array.getColor(R.styleable.PasswordPayLoadingView_load_success_color, Color.RED);
        loadFailureColor = array.getColor(R.styleable.PasswordPayLoadingView_load_failure_color, Color.RED);
        progressWidth = array.getDimension(R.styleable.PasswordPayLoadingView_progress_width, 10);
        progressRadius = array.getDimension(R.styleable.PasswordPayLoadingView_progress_radius, 100);
        array.recycle();

        initPaint();
        initPath();
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);    //设置画笔为圆角笔触
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
        failurePathLeft = new Path();
        failurePathRight = new Path();
    }

    private void initAnim() {
        circleAnimator = ValueAnimator.ofFloat(0, 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getPaddingLeft(), getPaddingTop());   //将当前画布的点移到getPaddingLeft,getPaddingTop,后面的操作都以该点作为参照点
        if (mStatus == StatusEnum.Loading) {    //正在加载
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }
            canvas.rotate(curAngle += 4, progressRadius, progressRadius);  //旋转的弧长为4
            canvas.drawArc(new RectF(0, 0, progressRadius * 2, progressRadius * 2), startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (mStatus == StatusEnum.LoadSuccess) {     //加载成功
            mPaint.setColor(loadSuccessColor);
            mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, progressRadius, Path.Direction.CW);
            mPathMeasure.setPath(mPathCircle, false);
            mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);   //截取path并保存到mPathCircleDst中
            canvas.drawPath(mPathCircleDst, mPaint);
            if (circleValue == 1) {      //表示圆画完了,可以钩了
                successPath.moveTo(getWidth() / 8 * 3, getWidth() / 2);
                successPath.lineTo(getWidth() / 2, getWidth() / 5 * 3);
                successPath.lineTo(getWidth() / 3 * 2, getWidth() / 5 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(successPath, false);
                mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);

                if (mLoadingListener != null) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingListener.loadingComplete(PasswordPayLoadingView.this, true);
                        }
                    }, 1000);

                }
            }
        } else {      //加载失败
            mPaint.setColor(loadFailureColor);
            mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, progressRadius, Path.Direction.CW);
            mPathMeasure.setPath(mPathCircle, false);
            mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, mPaint);

            if (circleValue == 1) {  //表示圆画完了,可以画叉叉的右边部分
                failurePathRight.moveTo(getWidth() / 3 * 2, getWidth() / 3);
                failurePathRight.lineTo(getWidth() / 3, getWidth() / 3 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathRight, false);
                mPathMeasure.getSegment(0, failValueRight * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);
            }

            if (failValueRight == 1) {    //表示叉叉的右边部分画完了,可以画叉叉的左边部分
                failurePathLeft.moveTo(getWidth() / 3, getWidth() / 3);
                failurePathLeft.lineTo(getWidth() / 3 * 2, getWidth() / 3 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathLeft, false);
                mPathMeasure.getSegment(0, failValueLeft * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);

                if (mLoadingListener != null) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingListener.loadingComplete(PasswordPayLoadingView.this, true);
                        }
                    }, 1000);

                }
            }
        }
    }

    private void setStatus(StatusEnum status) {
        mStatus = status;
    }

    public void loadLoading() {
        setStatus(StatusEnum.Loading);
        invalidate();
    }

    public void loadSuccess() {
        setStatus(StatusEnum.LoadSuccess);
        startSuccessAnim();
    }

    public void loadFailure() {
        setStatus(StatusEnum.LoadFailure);
        startFailAnim();
    }

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(circleAnimator);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void startFailAnim() {
        ValueAnimator failLeft = ValueAnimator.ofFloat(0f, 1.0f);
        failLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueRight = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator failRight = ValueAnimator.ofFloat(0f, 1.0f);
        failRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(failLeft).after(circleAnimator).before(failRight);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSpec(widthMeasureSpec, true), measureSpec(heightMeasureSpec, false));
    }

    private int measureSpec(int measureSpec, boolean isWidth) {
        int value;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            value = size;
        } else {
            int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
            value = (int) (2 * progressRadius + progressWidth + padding);
        }
        return value;
    }
}