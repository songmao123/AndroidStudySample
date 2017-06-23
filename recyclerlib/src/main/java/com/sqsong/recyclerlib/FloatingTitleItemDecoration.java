package com.sqsong.recyclerlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.sqsong.recyclerlib.util.ColorUtil;
import com.sqsong.recyclerlib.util.DensityUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 青松 on 2017/6/22.
 */

public class FloatingTitleItemDecoration extends RecyclerView.ItemDecoration {

    private int mTextSize;
    private int mLeftMargin;
    private int mTextHeight;
    private Paint mTextPaint;
    private int mBaseLineOffset;
    private int mBackgroundColor;
    private Paint mBackgroundPaint;
    private int mFloatingTitleHeight;
    private Rect mRectBounds = new Rect();
    private Map<Integer, String> mTitleMap;
    private Map<String, Integer> mIndexMap = new LinkedHashMap<>();

    public FloatingTitleItemDecoration(Context context, Map<Integer, String> titleMap) {
        this.mTitleMap = titleMap;
        init(context);
    }

    private void init(Context context) {
        mBackgroundColor = ContextCompat.getColor(context, R.color.colorBackground);
        mLeftMargin = context.getResources().getDimensionPixelSize(R.dimen.left_margin);
        mTextSize = DensityUtil.dip2px(12); //context.getResources().getDimensionPixelSize(R.dimen.default_text_size);
        mFloatingTitleHeight = context.getResources().getDimensionPixelSize(R.dimen.floating_title_height);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(Color.WHITE);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        mBaseLineOffset = (int) fontMetrics.bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        int top = mTitleMap.containsKey(position) ? mFloatingTitleHeight : 0;
        outRect.set(0, top, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int position = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewAdapterPosition();
            if (!mTitleMap.containsKey(position)) {
                continue;
            }
            drawTitleArea(c, parent, child, position);
        }
    }

    private void drawTitleArea(Canvas canvas, RecyclerView parent, View child, int position) {
        final int left = 0;
        final int right = parent.getWidth();
        parent.getDecoratedBoundsWithMargins(child, mRectBounds);
        final int top = mRectBounds.top;
        final int bottom = mRectBounds.top + mFloatingTitleHeight;
        mBackgroundPaint.setColor(mBackgroundColor);
        canvas.drawRect(left, top, right, bottom, mBackgroundPaint);

        ColorUtil.setPaintColor(mBackgroundPaint, position);
        int x = mLeftMargin + DensityUtil.dip2px(10);
        canvas.drawCircle(x, bottom - mFloatingTitleHeight / 2, DensityUtil.dip2px(10), mBackgroundPaint);

        String text = mTitleMap.get(position);
        float textWidth = mTextPaint.measureText(text);
        float baseY = bottom - (mFloatingTitleHeight - mTextHeight) / 2 - mBaseLineOffset;
        canvas.drawText(text, x - textWidth / 2, baseY, mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        View child = parent.findViewHolderForAdapterPosition(position).itemView;

        String title = getTitle(position);
        if (TextUtils.isEmpty(title)) return;

        boolean flag = false;
        if (getTitle(position + 1) != null && !title.equals(getTitle(position + 1))) {
            if (child.getHeight() + child.getTop() < mFloatingTitleHeight) {
                c.save();
                flag = true;
                c.translate(0, child.getHeight() + child.getTop() - mFloatingTitleHeight);
            }
        }

        mBackgroundPaint.setColor(mBackgroundColor);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mFloatingTitleHeight, mBackgroundPaint);

        ColorUtil.setPaintColor(mBackgroundPaint, getIndex(title));
        int x = mLeftMargin + DensityUtil.dip2px(10);
        c.drawCircle(x, mFloatingTitleHeight / 2, DensityUtil.dip2px(10), mBackgroundPaint);

        float textWidth = mTextPaint.measureText(title);
        c.drawText(title, x - textWidth / 2,
                parent.getPaddingTop() + mFloatingTitleHeight - (mFloatingTitleHeight - mTextHeight) / 2 - mBaseLineOffset, mTextPaint);

        if (flag) {
            c.restore();
        }
    }

    private String getTitle(int position) {
        while (position >= 0) {
            if (mTitleMap.containsKey(position)) {
                String str = mTitleMap.get(position);
                return str;
            }
            position--;
        }
        return null;
    }

    private int getIndex(String title) {
        if (mIndexMap.size() == 0) {
            for (Map.Entry<Integer, String> entry : mTitleMap.entrySet()) {
                String value = entry.getValue();
                Integer key = entry.getKey();
                mIndexMap.put(value, key);
            }
        }
        return mIndexMap.get(title);
    }
}
