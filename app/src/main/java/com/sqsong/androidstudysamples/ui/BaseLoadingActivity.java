package com.sqsong.androidstudysamples.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sqsong.androidstudysamples.R;
import com.sqsong.androidstudysamples.util.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 青松 on 2017/5/18.
 */

public abstract class BaseLoadingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolBar;

    private View mContentView;
    private FrameLayout mLoadingFrame;
    private FrameLayout mEmptyLoadingFrame;

    protected void beforeInflateView() {
    }

    protected abstract int getLayoutResId();

    protected void initView() {
    }

    protected abstract void initEvent();

    protected int getLoadingResId() {
        return -1;
    }

    protected int getEmptyLoadingResId() {
        return -1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeInflateView();
        setContentView(getLayoutResId());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null, false);
        mContentView = getLayoutInflater().inflate(layoutResID, null, false);

        setupContentView(baseView);
        setupLoadingEmptyView(baseView);
        setupLoadingView(baseView);

        getWindow().setContentView(baseView);
        ButterKnife.bind(this, baseView);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0);
        showContentView();
        initView();
        initEvent();
    }

    /**
     * 设置loading页面
     *
     * @param baseView
     */
    private void setupLoadingView(View baseView) {
        mLoadingFrame = (FrameLayout) baseView.findViewById(R.id.frame_loading);
        int loadingResId = getLoadingResId();
        View loadingView;
        if (loadingResId != -1) {
            loadingView = getLayoutInflater().inflate(loadingResId, null);
        } else {
            loadingView = getLayoutInflater().inflate(R.layout.layout_default_loading, null);
        }
        mLoadingFrame.addView(loadingView);
    }

    /**
     * 设置空白页
     *
     * @param baseView
     */
    private void setupLoadingEmptyView(View baseView) {
        // 添加空白页
        mEmptyLoadingFrame = (FrameLayout) baseView.findViewById(R.id.frame_loading_empty);
        int emptyLoadingResId = getEmptyLoadingResId();
        View emptyLoadingView;
        if (emptyLoadingResId != -1) {
            emptyLoadingView = getLayoutInflater().inflate(emptyLoadingResId, null);
        } else {
            emptyLoadingView = getLayoutInflater().inflate(R.layout.layout_default_loading_empty, null);
        }
        mEmptyLoadingFrame.addView(emptyLoadingView);
    }

    /**
     * 设置子类内容页
     *
     * @param baseView
     */
    private void setupContentView(View baseView) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
        RelativeLayout mContainer = (RelativeLayout) baseView.findViewById(R.id.container_rl);
        mContainer.addView(mContentView, 0);
    }

    protected void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setCenterToolbarTitle(@NonNull String text) {
        setToolbarTitle("");
        TextView titleText = (TextView) findViewById(R.id.center_title_tv);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(text);
    }

    /**
     * 设置标题栏显示文字
     *
     * @param text 需要显示的文字
     */
    protected void setToolbarTitle(@NonNull String text) {
        mToolBar.setTitle(text);
    }

    /**
     * 现在正在加载的view
     */
    protected void showLoadingView() {
        if (mLoadingFrame.getVisibility() != View.VISIBLE) {
            mLoadingFrame.setVisibility(View.VISIBLE);
        }
        if (mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.GONE);
        }
        if (mEmptyLoadingFrame.getVisibility() == View.VISIBLE) {
            mEmptyLoadingFrame.setVisibility(View.GONE);
        }
    }

    /**
     * 显示空白页面
     */
    protected void showLoadingEmptyView() {
        if (mLoadingFrame.getVisibility() == View.VISIBLE) {
            mLoadingFrame.setVisibility(View.GONE);
        }
        if (mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.GONE);
        }
        if (mEmptyLoadingFrame.getVisibility() == View.GONE) {
            mEmptyLoadingFrame.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示内容页
     */
    protected void showContentView() {
        if (mLoadingFrame.getVisibility() == View.VISIBLE) {
            mLoadingFrame.setVisibility(View.GONE);
        }
        if (mContentView.getVisibility() == View.GONE) {
            mContentView.setVisibility(View.VISIBLE);
        }
        if (mEmptyLoadingFrame.getVisibility() == View.VISIBLE) {
            mEmptyLoadingFrame.setVisibility(View.GONE);
        }
    }

}
