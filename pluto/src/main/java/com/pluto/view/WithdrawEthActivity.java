package com.pluto.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pluto.config.ClickType;
import com.pluto.config.Config;
import com.pluto.sdk.CommNetResponseListener;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

public class WithdrawEthActivity extends PlutoActivity {
    //
    private static final String TAG = "WithdrawEthActivity";
    //
    private TextView mTxtEthBalance;
    //
    private EditText mTxtEthWithdrawNum;
    //
    private TextView mTxtEthMini;
    //
    private EditText mTxtWalletAddress;
    //
    private TextView mTxtFeeNum;
    //
    private double mEthWithdrawValue = 0;
    //
    private String mWalletAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_eth_activity_low : R.layout.pluto_withdraw_eth_activity;
        setContentView(resId);
        // 设置点击输入框以外区域关闭键盘
        RelativeLayout layoutContent = findViewById(R.id.layout_main_content);
        setCloseKeyboard(layoutContent);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        mTxtEthBalance = findViewById(R.id.txt_eth_balance);
        mTxtEthWithdrawNum = findViewById(R.id.txt_eth_withdraw_num);
        mTxtEthMini = findViewById(R.id.txt_eth_mini);
        mTxtWalletAddress = findViewById(R.id.txt_wallet_address);
        mTxtFeeNum = findViewById(R.id.txt_fee_num);
        double ethBalance = CoreSDK.getInstance().getAccount().getEthBalance();
        double ethMiniWithdraw = CoreSDK.getInstance().getAccount().getEthMiniWithdraw();
        double ethFee = CoreSDK.getInstance().getAccount().getEthFee();
        mTxtEthBalance.setText(String.format(getString(R.string.pluto_balance), ConvertUtils.double2Decimal(ethBalance, 4, 2, true)));
        mTxtEthMini.setText(String.format(getString(R.string.pluto_minimum), ConvertUtils.double2Decimal(ethMiniWithdraw, 4, 2, true)));
        mTxtFeeNum.setText(String.format(getString(R.string.pluto_fee),  ConvertUtils.double2Decimal(ethFee, 4, 2, true)));
        mTxtEthWithdrawNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s == null || s.toString().equals("")) {
                        mEthWithdrawValue = 0;
                    } else {
                        mEthWithdrawValue = Double.parseDouble(s.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        mTxtWalletAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().equals("")) {
                    mWalletAddress = "";
                } else {
                    mWalletAddress = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        RelativeLayout layoutMaxContent = findViewById(R.id.layout_max_content);
        layoutMaxContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtEthWithdrawNum.setText(ConvertUtils.double2Decimal(ethBalance, 4, 2, false));
                mEthWithdrawValue = ethBalance;
            }
        });
        //
        RelativeLayout layoutTips = findViewById(R.id.layout_address_tips);
        layoutTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkBrowserUrl();
            }
        });
        //
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        Button btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdraw();
            }
        });
    }

    @Override
    protected void onKeyboardVisibility(boolean show) {
        super.onKeyboardVisibility(show);
        if (!show) {
            mTxtEthWithdrawNum.clearFocus();
            mTxtWalletAddress.clearFocus();
        }
    }

    /**
     *
     */
    private void withdraw() {
        if (mEthWithdrawValue < CoreSDK.getInstance().getAccount().getEthMiniWithdraw()) {
            Toast.makeText(this, "Withdraw amount is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        if (mWalletAddress.equals("")) {
            Toast.makeText(this, "Wallet address is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        WithdrawEthConfirmDialog dialog = new WithdrawEthConfirmDialog(this, new PlutoClickListener() {
            @Override
            public void onClick(ClickType type) {
                if (type == ClickType.SURE) {
                    confirmWithdraw();
                }
            }
        }, mEthWithdrawValue, CoreSDK.getInstance().getAccount().getEthFee(), mWalletAddress);
        dialog.show();
    }

    /**
     *
     */
    private void confirmWithdraw() {
        //
        CoreSDK.showLoading(this, 30);
        CoreSDK.getInstance().ethWithdraw(mEthWithdrawValue, mWalletAddress, new CommNetResponseListener() {
            @Override
            public void onResponse(boolean success, String message) {
                CoreSDK.hideLoading();
                if (WithdrawEthActivity.this.isDestroyed()) {
                    return;
                }
                //
                if (success) {
                    setResult(CoinAccountActivity.RESULT_CODE_WITHDRAW_SUCCESS);
                    finish();
                } else {
                    if (message != null && !message.equals("")) {
                        Toast.makeText(WithdrawEthActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     *
     */
    public void linkBrowserUrl() {
        try {
            Uri uri = Uri.parse(Config.GitbookUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Log.i(TAG, "link browser error");
        }
    }
}