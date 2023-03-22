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
import com.pluto.sdk.FishPreSwapResponseListener;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

public class WithdrawFishActivity extends PlutoActivity {
    //
    private static final String TAG = "WithdrawFishActivity";
    //
    private TextView mTxtFishBalance;
    //
    private EditText mTxtFishWithdrawNum;
    //
    private TextView mTxtFishMini;
    //
    private TextView mTxtEthNum;
    //
    private RelativeLayout mLayoutHaveAddress;
    //
    private ImageView mImgHaveAddress;
    //
    private RelativeLayout mLayoutNotAddress;
    //
    private ImageView mImgNotAddress;
    //
    private EditText mTxtWalletAddress;
    //
    private TextView mTxtFeeNum;
    //
    private long mFishInputValue = 0;
    //
    private long mFishWithdrawValue = 0;
    //
    private double mEthSwapValue = 0;
    //
    private String mWalletAddress = "";
    //1:PLUTO钱包，2:用户指定钱包
    private int mWithdrawType = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_fish_activity_low : R.layout.pluto_withdraw_fish_activity;
        setContentView(resId);
        // 设置点击输入框以外区域关闭键盘
        RelativeLayout layoutContent = findViewById(R.id.layout_main_content);
        setCloseKeyboard(layoutContent);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        mTxtFishBalance = findViewById(R.id.txt_fish_balance);
        mTxtFishWithdrawNum = findViewById(R.id.txt_fish_withdraw_num);
        mTxtFishMini = findViewById(R.id.txt_fish_mini);
        mTxtEthNum = findViewById(R.id.txt_eth_num);
        mLayoutHaveAddress = findViewById(R.id.layout_have_address);
        mImgHaveAddress = findViewById(R.id.img_have_address);
        mLayoutNotAddress = findViewById(R.id.layout_not_address);
        mImgNotAddress = findViewById(R.id.img_not_address);
        mTxtWalletAddress = findViewById(R.id.txt_wallet_address);
        mTxtFeeNum = findViewById(R.id.txt_fee_num);
        long fishBalance = CoreSDK.getInstance().getAccount().getFishBalance();
        int fishMiniWithdraw = CoreSDK.getInstance().getAccount().getFishMiniWithdraw();
        int fishFee = CoreSDK.getInstance().getAccount().getFishFee();
        mTxtFishBalance.setText(String.format(getString(R.string.pluto_balance), ConvertUtils.num2Thousandth(fishBalance)));
        mTxtFishMini.setText(String.format(getString(R.string.pluto_minimum), ConvertUtils.num2Thousandth(fishMiniWithdraw)));
        mTxtFeeNum.setText(String.format(getString(R.string.pluto_fee),  ConvertUtils.num2Thousandth(fishFee)));
        mTxtFishWithdrawNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().equals("")) {
                    mFishInputValue = 0;
                } else {
                    mFishInputValue = Long.parseLong(s.toString());
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
                mTxtFishWithdrawNum.setText(String.valueOf(fishBalance));
                mFishInputValue = fishBalance;
                checkFishWithdraw();
            }
        });
        //
        RelativeLayout layoutAddressTips = findViewById(R.id.layout_address_tips);
        layoutAddressTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkBrowserUrl();
            }
        });
        //
        mLayoutHaveAddress.setEnabled(false);
        mLayoutHaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWithdrawType = 2;
                mLayoutHaveAddress.setEnabled(false);
                mLayoutNotAddress.setEnabled(true);
                mImgHaveAddress.setBackgroundResource(R.drawable.pluto_radio_choose);
                mImgNotAddress.setBackgroundResource(R.drawable.pluto_radio_unchoose);
                mTxtWalletAddress.setEnabled(true);
                mTxtWalletAddress.setText("");
            }
        });
        //
        mLayoutNotAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWithdrawType = 1;
                mLayoutHaveAddress.setEnabled(true);
                mLayoutNotAddress.setEnabled(false);
                mImgHaveAddress.setBackgroundResource(R.drawable.pluto_radio_unchoose);
                mImgNotAddress.setBackgroundResource(R.drawable.pluto_radio_choose);
                mTxtWalletAddress.setEnabled(false);
                mTxtWalletAddress.setText(CoreSDK.getInstance().getAccount().getWalletAddress());
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
            mTxtFishWithdrawNum.clearFocus();
            mTxtWalletAddress.clearFocus();
            //
            checkFishWithdraw();
        }
    }

    /**
     *
     */
    private void checkFishWithdraw() {
        //
        if (mFishInputValue == mFishWithdrawValue) {
            return;
        }
        //
        if (mFishInputValue == 0) {
            mFishWithdrawValue = 0;
            mTxtEthNum.setHint("0");
            return;
        }
        //
        if (mFishInputValue < CoreSDK.getInstance().getAccount().getFishMiniWithdraw()) {
            return;
        }
        //
        CoreSDK.showLoading(this, 30);
        CoreSDK.getInstance().getFishPreSwapInfo(mFishInputValue, new FishPreSwapResponseListener() {
            @Override
            public void onResponse(boolean success, double ethAmount, String message) {
                CoreSDK.hideLoading();
                if (WithdrawFishActivity.this.isDestroyed()) {
                    return;
                }
                //
                if (!success) {
                    if (message != null && !message.equals("")) {
                        Toast.makeText(WithdrawFishActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                //
                mFishWithdrawValue = mFishInputValue;
                mEthSwapValue = ethAmount;
                mTxtEthNum.setText(ConvertUtils.double2Decimal(ethAmount, 4, 2, true));
            }
        });
    }

    /**
     * 提现
     */
    private void withdraw() {
        if (mFishWithdrawValue < CoreSDK.getInstance().getAccount().getFishMiniWithdraw() || mEthSwapValue <= 0) {
            Toast.makeText(this, "Withdraw amount is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        if (mWithdrawType == 2 && mWalletAddress.equals("")) {
            Toast.makeText(this, "Wallet address is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        WithdrawFishConfirmDialog dialog = new WithdrawFishConfirmDialog(this, new PlutoClickListener() {
            @Override
            public void onClick(ClickType type) {
                if (type == ClickType.SURE) {
                    confirmWithdraw();
                }
            }
        }, mFishWithdrawValue, mEthSwapValue, CoreSDK.getInstance().getAccount().getFishFee(), mWalletAddress);
        dialog.show();
    }

    /**
     *
     */
    private void confirmWithdraw() {
        CoreSDK.showLoading(this, 30);
        CoreSDK.getInstance().fishWithdraw(mFishWithdrawValue, mEthSwapValue, mWithdrawType, mWalletAddress, new CommNetResponseListener() {
            @Override
            public void onResponse(boolean success, String message) {
                CoreSDK.hideLoading();
                if (WithdrawFishActivity.this.isDestroyed()) {
                    return;
                }
                //
                if (success) {
                    if (mWithdrawType == 1) {
                        waitWithdrawProgress();
                    } else {
                        setResult(CoinAccountActivity.RESULT_CODE_WITHDRAW_SUCCESS);
                        finish();
                    }
                } else {
                    if (message != null && !message.equals("")) {
                        Toast.makeText(WithdrawFishActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     *
     */
    private void waitWithdrawProgress() {
        WithdrawFishProgressDialog dialog = new WithdrawFishProgressDialog(this, new PlutoClickListener() {
            @Override
            public void onClick(ClickType type) {
                setResult(CoinAccountActivity.RESULT_CODE_WITHDRAW_SUCCESS);
                finish();
            }
        });
        dialog.show();
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