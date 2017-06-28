package com.sqsong.androidstudysamples.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sqsong.androidstudysamples.R;
import com.sqsong.indicator.CirclePagerIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleIndicatorActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.indicator)
    CirclePagerIndicator mPagerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_indicator);
        ButterKnife.bind(this);

        initEvent();
    }

    private void initEvent() {
        GalleryAdapter mAdapter = new GalleryAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mPagerIndicator.setViewPager(mViewPager);
    }

    class GalleryAdapter extends PagerAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private int[] imageArray = {R.drawable.image01, R.drawable.image02, R.drawable.image03,
                R.drawable.image04, R.drawable.image05};

        public GalleryAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.item_gallery, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.gallery_image);
            Glide.with(mContext).load(imageArray[position]).into(imageView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
