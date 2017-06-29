package com.sqsong.androidstudysamples.ui;

import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sqsong.passwordlib.AliPayPasswordDialog;
import com.sqsong.passwordlib.PasswordEditView;
import com.sqsong.androidstudysamples.R;
import com.sqsong.recyclerlib.util.DensityUtil;

import butterknife.BindView;

public class PasswordActivity extends BaseLoadingActivity implements View.OnClickListener {

    @BindView(R.id.pswEdit1)
    PasswordEditView mEdit1;

    @BindView(R.id.pswEdit2)
    PasswordEditView mEdit2;

    @BindView(R.id.button)
    Button mButton;

    private AliPayPasswordDialog mPasswordDialog;

    private PasswordEditView.PasswordEditViewCallback mCallback = new PasswordEditView.PasswordEditViewCallback() {
        @Override
        public void onPasswordComplete(PasswordEditView passwordEditView, String password) {
            Toast.makeText(PasswordActivity.this, password, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_password;
    }

    @Override
    protected void initEvent() {
        setupToolbar();
        setToolbarTitle("Password Activity");

        mEdit1.setPasswordEditViewCallback(mCallback);
        mEdit2.setPasswordEditViewCallback(mCallback);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                showPasswordDialog();
                break;
        }
    }

    private void showPasswordDialog() {
        mPasswordDialog = new AliPayPasswordDialog.Builder(this)
//                .titleAreaColor(R.color.colorAccent)
//                .titleAreaBackgroundColor(R.color.colorPrimaryDark)
//                .contentTextSize(16)
//                .contentTextColor(R.color.colorRed)
//                .forgetPswTextColor(R.color.colorAccent)
//                .forgetPswTextSize(12)
                .title("请输入支付密码").content("您需支付: 100元").show();
        mPasswordDialog.setPasswordDialogListener(new AliPayPasswordDialog.PasswordDialogListener() {
            @Override
            public void onPasswordComplete(DialogFragment dialog, String password) {
                Toast.makeText(PasswordActivity.this, password, Toast.LENGTH_SHORT).show();
                mButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPasswordDialog.setPayLoadingStatus(true);
                    }
                }, 5000);
            }

            @Override
            public void loadingAnimComplete(DialogFragment dialog) {
                dialog.dismiss();
            }

            @Override
            public void onForgetPasswordClick(DialogFragment dialog) {
                Toast.makeText(PasswordActivity.this, "forget password click.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
