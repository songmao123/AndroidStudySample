package com.sqsong.androidstudysamples.ui;

import com.sqsong.androidstudysamples.R;
import com.sqsong.circletext.CircleTextView;

import butterknife.BindView;

public class CircleTextActivity extends BaseLoadingActivity {

    @BindView(R.id.circleTextView)
    CircleTextView mCircleTextView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_circle_text;
    }

    @Override
    protected void initEvent() {
        setupToolbar();
        setToolbarTitle("CircleTextSample");
        mCircleTextView.setText("宋");
    }

}
