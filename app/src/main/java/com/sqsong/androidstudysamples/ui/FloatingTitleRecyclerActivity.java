package com.sqsong.androidstudysamples.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sqsong.androidstudysamples.R;
import com.sqsong.androidstudysamples.adapter.PhoneCodeAdapter;
import com.sqsong.androidstudysamples.bean.PhoneAreaCodeData;
import com.sqsong.androidstudysamples.db.PhoneCodeDbManager;
import com.sqsong.recyclerlib.FloatingTitleItemDecoration;
import com.sqsong.recyclerlib.SideBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

public class FloatingTitleRecyclerActivity extends BaseLoadingActivity implements PhoneCodeAdapter.PhoneCodeItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.sideBar)
    SideBar mSideBar;

    private PhoneCodeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<PhoneAreaCodeData> mCodeList = new ArrayList<>();
    private LinkedHashMap<Integer, String> mHeaderMap = new LinkedHashMap<>();
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showContentView();
            mSideBar.setIndexMap(mHeaderMap);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_floating_title_recycler;
    }

    @Override
    protected void initEvent() {
        setupToolbar();
        setToolbarTitle("Floating Title RecyclerView");

        mAdapter = new PhoneCodeAdapter(this, mCodeList);
        mAdapter.setPhoneCodeItemClickListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new FloatingTitleItemDecoration(this, mHeaderMap));
        mRecyclerView.setAdapter(mAdapter);
        mSideBar.setRecyclerView(mRecyclerView);

        fetchCodeData();
    }

    private void fetchCodeData() {
        showLoadingView();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<PhoneAreaCodeData> areaCodeList = PhoneCodeDbManager.getInstance().queryAreaCodeList(0);
                mCodeList.clear();
                mCodeList.addAll(areaCodeList);
                for (int i = 0; i < areaCodeList.size() - 1; i++) {
                    String curCode = areaCodeList.get(i).getChinesePinyin().substring(0, 1);
                    if (!mHeaderMap.containsValue(curCode)) {
                        mHeaderMap.put(i, curCode);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void onPhoneCodeItemClicked(PhoneAreaCodeData codeData) {

    }
}
