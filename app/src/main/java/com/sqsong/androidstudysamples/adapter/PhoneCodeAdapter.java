package com.sqsong.androidstudysamples.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sqsong.androidstudysamples.R;
import com.sqsong.androidstudysamples.bean.PhoneAreaCodeData;
import com.sqsong.androidstudysamples.util.ColorGenerator;
import com.sqsong.circletext.CircleTextView;

import java.util.List;


/**
 * Created by 青松 on 2017/5/17.
 */

public class PhoneCodeAdapter extends RecyclerView.Adapter<PhoneCodeAdapter.PhoneCodeViewHolder> {

    private LayoutInflater mInflater;
    private List<PhoneAreaCodeData> mCodeList;
    private PhoneCodeItemClickListener mListener;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    public void setPhoneCodeItemClickListener(PhoneCodeItemClickListener l) {
        this.mListener = l;
    }

    public interface PhoneCodeItemClickListener {
        void onPhoneCodeItemClicked(PhoneAreaCodeData codeData);
    }

    public PhoneCodeAdapter(Context context, List<PhoneAreaCodeData> codeList) {
        this.mCodeList = codeList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public PhoneCodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_phone_code_item, parent, false);
        return new PhoneCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhoneCodeViewHolder holder, int position) {
        final PhoneAreaCodeData codeData = mCodeList.get(position);

        String name = codeData.getCnName().replace("#", "");
        int color = mColorGenerator.getColor(codeData.getCnName());
        holder.circleTextView.setBackgroundColor(color);
        holder.circleTextView.setText(name.substring(0, 1));

        holder.nameTv.setText(name);
        holder.codeTv.setText("+" + codeData.getCode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPhoneCodeItemClicked(codeData);
                }
            }
        });
    }

    @Override
    public int getItemCount()  {
        return mCodeList.size();
    }

    class PhoneCodeViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView nameTv;
        TextView codeTv;
        CircleTextView circleTextView;
        public PhoneCodeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.nameTv = (TextView) itemView.findViewById(R.id.name);
            this.codeTv = (TextView) itemView.findViewById(R.id.code);
            this.circleTextView = (CircleTextView) itemView.findViewById(R.id.circleTextView);
        }
    }
}