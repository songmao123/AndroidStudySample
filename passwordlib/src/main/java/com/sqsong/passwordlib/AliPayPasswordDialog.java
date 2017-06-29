package com.sqsong.passwordlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by 青松 on 2017/5/31.
 */
public class AliPayPasswordDialog extends DialogFragment implements View.OnClickListener,
        PasswordEditView.PasswordEditViewCallback, PasswordPayLoadingView.LoadingCompleteListener {

    private TextView mTitleTv;
    private ImageView mCloseBtn;
    private TextView mForgetPswTv;
    private TextView mMessageText;
    private LinearLayout mPasswordAreaLl;
    private PasswordEditView mPswEditView;
    private PasswordPayLoadingView mPayLoadingView;

    private View mRootView;
    private Builder mBuilder;
    private PasswordDialogListener mListener;
    private RelativeLayout mTitleArea;

    public static AliPayPasswordDialog newInstance() {
        return new AliPayPasswordDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mBuilder = bundle.getParcelable("builder");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.PasswordDialogAnimStyle;
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().getAttributes().gravity = Gravity.BOTTOM;
        setCancelable(false);

        mRootView = inflater.inflate(R.layout.dialog_pay_password, container, false);
        initView(mRootView);
        return mRootView;
    }

    private void initView(View rootView) {
        mTitleArea = (RelativeLayout) rootView.findViewById(R.id.title_rl);
        mCloseBtn = (ImageView) rootView.findViewById(R.id.close_iv);
        mTitleTv = (TextView) rootView.findViewById(R.id.title_tv);
        mMessageText = (TextView) rootView.findViewById(R.id.message_tv);
        mPswEditView = (PasswordEditView) rootView.findViewById(R.id.password_edit_view);
        mForgetPswTv = (TextView) rootView.findViewById(R.id.forget_psw_tv);
        mPasswordAreaLl = (LinearLayout) rootView.findViewById(R.id.password_ll);
        mPayLoadingView = (PasswordPayLoadingView) rootView.findViewById(R.id.pay_loading_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        if (layoutParams != null) {
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            layoutParams.width = screenWidth;
        }

        mCloseBtn.setOnClickListener(this);
        mForgetPswTv.setOnClickListener(this);
        mPswEditView.setPasswordEditViewCallback(this);
        mPayLoadingView.setLoadingCompleteListener(this);

        if (mBuilder != null) {
            applyBuilderParams();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyBuilderParams() {
        mTitleTv.setText(mBuilder.title);
        mMessageText.setText(mBuilder.content);

        int titleAreaBackgroundColor = mBuilder.titleAreaBackgroundColor;
        if (titleAreaBackgroundColor != 0) {
            mTitleArea.setBackgroundResource(titleAreaBackgroundColor);
        }

        int titleAreaColor = mBuilder.titleAreaColor;
        if (titleAreaColor != 0) {
            int color = ContextCompat.getColor(getContext(), titleAreaColor);
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_enabled},
                    new int[]{-android.R.attr.state_enabled}};
            int[] colors = new int[]{color, color};
            mCloseBtn.setImageTintList(new ColorStateList(states, colors));
            mTitleTv.setTextColor(color);
        }

        float textSize = mBuilder.contentTextSize;
        if (textSize != 0) {
            mMessageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }

        int contentColor = mBuilder.contentTextColor;
        if (contentColor != 0) {
            int color = ContextCompat.getColor(getContext(), contentColor);
            mMessageText.setTextColor(color);
        }

        float forgetPswTextSize = mBuilder.forgetPswTextSize;
        if (forgetPswTextSize != 0) {
            mForgetPswTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, forgetPswTextSize);
        }

        int forgetPswTextColor = mBuilder.forgetPswTextColor;
        if (forgetPswTextColor != 0) {
            int color = ContextCompat.getColor(getContext(), forgetPswTextColor);
            mForgetPswTv.setTextColor(color);
        }
    }

    @Override
    public void onPasswordComplete(PasswordEditView passwordEditView, String password) {
        if (mListener != null) {
            mListener.onPasswordComplete(this, password);
        }
        InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mPswEditView.getWindowToken(), 0);
        mPasswordAreaLl.setVisibility(View.INVISIBLE);
        mPasswordAreaLl.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPasswordAreaLl.setVisibility(View.GONE);
                mPayLoadingView.setVisibility(View.VISIBLE);
                mPayLoadingView.loadLoading();
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close_iv) {
            dismiss();
        } else if (id == R.id.forget_psw_tv) {
            if (mListener != null) {
                mListener.onForgetPasswordClick(this);
            }
        }
    }

    public void setPasswordDialogListener(PasswordDialogListener l) {
        this.mListener = l;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void loadingComplete(View view, boolean success) {
        if (mListener != null) {
            mListener.loadingAnimComplete(this);
        }
    }

    public void setPayLoadingStatus(boolean success) {
        if (success) {
            mPayLoadingView.loadSuccess();
        } else {
            mPayLoadingView.loadFailure();
        }
    }

    public interface PasswordDialogListener {
        void onPasswordComplete(DialogFragment dialog, String password);

        void loadingAnimComplete(DialogFragment dialog);

        void onForgetPasswordClick(DialogFragment dialog);
    }

    public static class Builder implements Parcelable {

        private String title;
        private String content;
        private int titleAreaColor;
        private int contentTextColor;
        private float contentTextSize;
        private int forgetPswTextColor;
        private float forgetPswTextSize;
        private int titleAreaBackgroundColor;
        private FragmentActivity activity;

        public Builder(FragmentActivity activity) {
            this.activity = activity;
        }


        protected Builder(Parcel in) {
            title = in.readString();
            content = in.readString();
            contentTextColor = in.readInt();
            titleAreaColor = in.readInt();
            contentTextSize = in.readFloat();
            forgetPswTextColor = in.readInt();
            forgetPswTextSize = in.readFloat();
            titleAreaBackgroundColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(content);
            dest.writeInt(contentTextColor);
            dest.writeInt(titleAreaColor);
            dest.writeFloat(contentTextSize);
            dest.writeInt(forgetPswTextColor);
            dest.writeFloat(forgetPswTextSize);
            dest.writeInt(titleAreaBackgroundColor);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder titleAreaColor(int color) {
            this.titleAreaColor = color;
            return this;
        }

        public Builder titleAreaBackgroundColor(int color) {
            this.titleAreaBackgroundColor = color;
            return this;
        }

        public Builder contentTextColor(int color) {
            this.contentTextColor = color;
            return this;
        }

        public Builder contentTextSize(float textSize) {
            this.contentTextSize = textSize;
            return this;
        }

        public Builder forgetPswTextColor(int color) {
            this.forgetPswTextColor = color;
            return this;
        }

        public Builder forgetPswTextSize(float textSize) {
            this.forgetPswTextSize = textSize;
            return this;
        }

        public AliPayPasswordDialog show() {
            if (activity == null) {
                throw new IllegalArgumentException("The activity must not be null. and must be the instance of FragmentActivity.");
            }
            AliPayPasswordDialog dialog = AliPayPasswordDialog.newInstance();
            Bundle bundle = new Bundle();
            bundle.putParcelable("builder", this);
            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), "");
            return dialog;
        }
    }

}
