package com.sqsong.recyclerlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sqsong.recyclerlib.util.DensityUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 青松 on 2017/6/23.
 */

public class SideBar extends View {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private float mTextSize;
    private int mSingleHeight;
    private Paint mTouchedPaint; // 触摸SideBar时，绘制字母的画笔
    private int mTouchedTextColor; // 触摸SideBar时，所触摸到的字母颜色
    private int mNormalIndexColor; // 正常未触摸SideBar，所有字母的颜色
    private int mTouchedIndexColor; // 触摸SideBar时，所有字母颜色
    private int mTouchedIndex = -1; // 触摸到的字母的位置
    private RecyclerView mRecyclerView;
    private Map<Integer, String> mHeadMap;
    private String mIndexStr = "#ABCDEFGHIJKLMNOPQRSTUVWXY";
    private Map<String, Integer> mIndexHashMap = new LinkedHashMap<>();

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
            mNormalIndexColor = typedArray.getColor(R.styleable.SideBar_normalIndexColor, Color.GRAY);
            mTouchedIndexColor = typedArray.getColor(R.styleable.SideBar_touchedIndexColor, Color.BLACK);
            mTouchedTextColor = typedArray.getColor(R.styleable.SideBar_touchedTextColor, Color.RED);
            mTextSize = typedArray.getDimension(R.styleable.SideBar_textSize, DensityUtil.dip2px(14));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mNormalIndexColor);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextSize(mTextSize);

        mTouchedPaint = new Paint();
        mTouchedPaint.setDither(true);
        mTouchedPaint.setAntiAlias(true);
        mTouchedPaint.setColor(mTouchedTextColor);
        mTouchedPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTouchedPaint.setTextSize(DensityUtil.dip2px(14));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h - DensityUtil.dip2px(60) * 2;
        mWidth = w;
        mSingleHeight = mHeight / mIndexStr.length();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mIndexStr.length(); i++) {
            String index = mIndexStr.substring(i, i + 1);
            float x = (mWidth - mPaint.measureText(index)) / 2;
            if (i == mTouchedIndex) {
                canvas.drawText(index, x, mSingleHeight * (i + 1) + DensityUtil.dip2px(60), mTouchedPaint);
            } else {
                canvas.drawText(index, x, mSingleHeight * (i + 1) + DensityUtil.dip2px(60), mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchedPaint.setColor(mTouchedTextColor);
                mPaint.setColor(mTouchedIndexColor);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                doMoveAction(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mPaint.setColor(mNormalIndexColor);
                mTouchedPaint.setColor(mNormalIndexColor);
                ((IndexBar) getParent()).hideIndexCircle();
                invalidate();
                break;
        }
        return true;
    }

    private void doMoveAction(float y) {
        int length = mIndexStr.toCharArray().length;
        // 计算出触摸时对应的字母index的位置， 由于上下有间隔，所以该位置有可能越界， mTouchedIndex对边界做检测
        int position = (int) ((y - getTop() - DensityUtil.dip2px(60)) / mHeight * length);
        mTouchedIndex = position;
        if (mTouchedIndex < 0) {
            mTouchedIndex = 0;
        }
        if (mTouchedIndex > length - 1) {
            mTouchedIndex = length - 1;
        }

        IndexBar indexBar = (IndexBar) getParent();
        String text = String.valueOf(mIndexStr.toCharArray()[mTouchedIndex]);
        // 通知父IndexBar刷新显示导航字母
        indexBar.setDrawData(text, y, mTouchedIndex);

        scrollRecyclerView(text);
        invalidate();
    }

    /**
     * {@link RecyclerView}滚动到对应的位置。 由于mHeadMap的key和value分别记录的是数据集中起始字母的位置和起始字母，
     * 所以当{@link SideBar}获取到对应位置的字母后， 将mHeadMap中的key、value反转，通过字母就可以获取到该字母在数据集
     * 中的具体位置。
     * @param text 触摸到的字母
     */
    private void scrollRecyclerView(String text) {
        if (mRecyclerView == null || mHeadMap == null) return;
        if (mIndexHashMap.size() == 0) {
            for (Map.Entry<Integer, String> entry: mHeadMap.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();
                mIndexHashMap.put(value, key);
            }
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(mIndexHashMap.get(text), 0);
    }

    /**
     * 设置装有字母在数据集中的位置和index字母的map集合。
     * @param indexMap map集合
     */
    public void setIndexMap(Map<Integer, String> indexMap) {
        if (indexMap == null) return;

        this.mHeadMap = indexMap;
        Collection<String> values = indexMap.values();
        StringBuffer buffer = new StringBuffer();
        for (String str : values) {
            buffer.append(str);
        }
        this.mIndexStr = buffer.toString();
    }

    /**
     * 设置{@link RecyclerView}.
     * @param recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
}
