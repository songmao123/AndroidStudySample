package com.sqsong.androidstudysamples.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sqsong.androidstudysamples.R;
import com.sqsong.androidstudysamples.ui.CircleIndicatorActivity;
import com.sqsong.androidstudysamples.ui.CircleTextActivity;
import com.sqsong.androidstudysamples.ui.FloatingTitleRecyclerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 青松 on 2017/6/22.
 */

public class ClazzListAdapter extends RecyclerView.Adapter<ClazzListAdapter.ClazzListViewHolder> {

    public static final Class[] sClazzs = {CircleTextActivity.class, FloatingTitleRecyclerActivity.class, CircleIndicatorActivity.class};

    private LayoutInflater mInflater;
    private ClazzListItemClickListener mListener;

    public void setClazzListItemClickListener(ClazzListItemClickListener l) {
        this.mListener = l;
    }

    public interface ClazzListItemClickListener {
        void onClazzListItemClick(Class clazz);
    }

    public ClazzListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ClazzListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_clazz_list, parent, false);
        return new ClazzListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClazzListViewHolder holder, final int position) {
        holder.textView.setText(sClazzs[position].getSimpleName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClazzListItemClick(sClazzs[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sClazzs.length;
    }


    class ClazzListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;

        View itemView;

        public ClazzListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

}
