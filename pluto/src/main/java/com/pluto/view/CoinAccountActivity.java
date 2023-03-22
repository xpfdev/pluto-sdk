package com.pluto.view;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pluto.sdk.CommNetResponseListener;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CoinAccountActivity extends PlutoActivity {
    //
    private static final String TAG = "CoinAccountActivity";
    //
    private static final int REQ_WITHDRAW_FISH = 1000;
    //
    private static final int REQ_WITHDRAW_ETH = 2000;
    //
    protected static final int RESULT_CODE_WITHDRAW_SUCCESS = 1;
    //
    private TextView mTxtFishBalance;
    //
    private TextView mTxtFishWorth;
    //
    private ImageView mImgCat;
    //
    private ProgressBar mBarFish;
    //
    private TextView mTxtFishPercent;
    //
    private RelativeLayout mLayoutCountdown;
    //
    private TextView mTxtDaysNum;
    //
    private TextView mTxtDaysVal;
    //
    private TextView mTxtHoursNum;
    //
    private TextView mTxtHoursVal;
    //
    private TextView mTxtMinsNum;
    //
    private TextView mTxtMinsVal;
    //
    private TextView mTxtSecondNum;
    //
    private TextView mTxtSecondVal;
    //
    private Button mBtnCashOut;
    //
    private RelativeLayout mLayoutEthContent;
    //
    private TextView mTxtEthBalance;
    //
    private TextView mTxtEthWorth;
    //
    private TextView mTxtEthMinWithdraw;
    //
    private long mLeftSeconds = 0;
    //
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_coin_account_activity_low : R.layout.pluto_coin_account_activity;
        setContentView(resId);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        mTxtFishBalance = findViewById(R.id.txt_fish_balance);
        mTxtFishWorth = findViewById(R.id.txt_fish_worth);
        mImgCat = findViewById(R.id.img_cat);
        mBarFish = findViewById(R.id.bar_fish);
        mTxtFishPercent = findViewById(R.id.txt_fish_percent);
        mLayoutCountdown = findViewById(R.id.layout_countdown);
        mTxtDaysNum = findViewById(R.id.txt_days_num);
        mTxtDaysVal = findViewById(R.id.txt_days_val);
        mTxtHoursNum = findViewById(R.id.txt_hours_num);
        mTxtHoursVal = findViewById(R.id.txt_hours_val);
        mTxtMinsNum = findViewById(R.id.txt_mins_num);
        mTxtMinsVal = findViewById(R.id.txt_mins_val);
        mTxtSecondNum = findViewById(R.id.txt_second_num);
        mTxtSecondVal = findViewById(R.id.txt_second_val);
        mBtnCashOut = findViewById(R.id.btn_cash_out);
        mLayoutEthContent = findViewById(R.id.layout_eth_content);
        mTxtEthBalance = findViewById(R.id.txt_eth_balance);
        mTxtEthWorth = findViewById(R.id.txt_eth_worth);
        mTxtEthMinWithdraw = findViewById(R.id.txt_eth_min_withdraw);
        //
        mBtnCashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashOut();
            }
        });
        //
        Button btnEthClaim = findViewById(R.id.btn_eth_claim);
        btnEthClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claim();
            }
        });
        //
        TextView txtHistory = findViewById(R.id.txt_history);
        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history();
            }
        });
        //先使用钱包数据展示界面，随后拉取最新的钱包数据
        updateView();
        updateWalletInfo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_WITHDRAW_FISH || requestCode == REQ_WITHDRAW_ETH) {
            if (resultCode == RESULT_CODE_WITHDRAW_SUCCESS) {
                updateWalletInfo(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     *
     */
    private void updateWalletInfo(boolean showLoading) {
        if (showLoading) {
            CoreSDK.showLoading(this, 30);
        }
        //
        CoreSDK.getInstance().getWalletInfo(new CommNetResponseListener() {
            @Override
            public void onResponse(boolean success, String message) {
                CoreSDK.hideLoading();
                if (CoinAccountActivity.this.isDestroyed()) {
                    return;
                }
                updateView();
            }
        });
    }

    /**
     *
     */
    private void updateView() {
        long fishBalance = CoreSDK.getInstance().getAccount().getFishBalance();
        int fishMiniWithdraw = CoreSDK.getInstance().getAccount().getFishMiniWithdraw();
        double fishBalance2Eth = CoreSDK.getInstance().getAccount().getFishBalance2Eth();
        mTxtFishBalance.setText(ConvertUtils.num2Thousandth(fishBalance));
        mTxtFishWorth.setText(String.format(getString(R.string.pluto_fish_2_eth_worth), ConvertUtils.double2Decimal(fishBalance2Eth, 4, 2, true)));
        //延时检测进度条，否则获取不到进度条实际宽度
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CoinAccountActivity.this.isDestroyed()) {
                    return;
                }
                checkBar(fishBalance, fishMiniWithdraw);
            }
        }, 100);
        //
        if (fishBalance >= fishMiniWithdraw) {
            mBtnCashOut.setBackgroundResource(R.drawable.pluto_button2);
            mBtnCashOut.setEnabled(true);
        } else {
            mBtnCashOut.setBackgroundResource(R.drawable.pluto_button3);
            mBtnCashOut.setEnabled(false);
        }
        //
        long fishNextWithdrawTime = CoreSDK.getInstance().getAccount().getFishNextWithdrawTime();
        long curTime = new Date().getTime();
        if (fishNextWithdrawTime > curTime) {
            //加10s误差，防止时间到了无法提现
            mLeftSeconds = (fishNextWithdrawTime - curTime) / 1000 + 10;
        }
        checkTime();
        //
        double ethBalance = CoreSDK.getInstance().getAccount().getEthBalance();
        if (ethBalance <= 0) {
            mLayoutEthContent.setVisibility(View.INVISIBLE);
            return;
        }
        double ethBalanceWorth = CoreSDK.getInstance().getAccount().getEthBalanceWorth();
        double ethMiniWithdraw = CoreSDK.getInstance().getAccount().getEthMiniWithdraw();
        mLayoutEthContent.setVisibility(View.VISIBLE);
        mTxtEthBalance.setText(ConvertUtils.double2Decimal(ethBalance, 4, 2, true));
        mTxtEthWorth.setText(String.format(getString(R.string.pluto_eth_2_dollar_worth), ConvertUtils.double2Decimal(ethBalanceWorth, 2, 2, true)));
        mTxtEthMinWithdraw.setText(String.format(getString(R.string.pluto_eth_mini_withdraw), ConvertUtils.double2Decimal(ethMiniWithdraw, 4, 2, true)));
    }

    /**
     * 检测进度条
     * @param fishBalance
     * @param fishMiniWithdraw
     */
    private void checkBar(long fishBalance, int fishMiniWithdraw) {
        //由于要获取进度条实际宽度，此处要延时执行，否则获取宽度为0
        int catRes = R.drawable.pluto_cat_ani1;
        if (fishBalance >= fishMiniWithdraw * 0.5) {
            catRes = R.drawable.pluto_cat_ani3;
        } else if (fishBalance >= fishMiniWithdraw * 0.33) {
            catRes = R.drawable.pluto_cat_ani2;
        }
        mImgCat.setImageResource(catRes);
        AnimationDrawable aniCat = (AnimationDrawable) mImgCat.getDrawable();
        aniCat.start();
        mTxtFishPercent.setText(fishBalance + "/" + fishMiniWithdraw);
        float percent = fishBalance >= fishMiniWithdraw ? 1.0f : fishBalance / (fishMiniWithdraw * 1.0f);
        int value = (int)(percent * 100);
        mBarFish.setProgress(value);
        mImgCat.setX(percent * mBarFish.getWidth());
    }

    /**
     *
     */
    private void checkTime() {
        if (mLeftSeconds <= 0) {
            mBtnCashOut.setVisibility(View.VISIBLE);
            mLayoutCountdown.setVisibility(View.INVISIBLE);
            return;
        }
        //
        mBtnCashOut.setVisibility(View.INVISIBLE);
        mLayoutCountdown.setVisibility(View.VISIBLE);
        //
        formatTime();
        startTimer();
    }

    /**
     *
     */
    private void formatTime() {
        long days = mLeftSeconds / 86400;
        long hours = mLeftSeconds % 86400 / 3600;
        long mins = mLeftSeconds % 86400 % 3600 / 60;
        long seconds = mLeftSeconds % 86400 % 3600 % 60;
        String strDay = days < 10 ? "0" + days : days + "";
        String strHour = hours < 10 ? "0" + hours : hours + "";
        String strMin = mins < 10 ? "0" + mins : mins + "";
        String strSecond = seconds < 10 ? "0" + seconds : seconds + "";
        mTxtDaysNum.setText(strDay);
        mTxtHoursNum.setText(strHour);
        mTxtMinsNum.setText(strMin);
        mTxtSecondNum.setText(strSecond);
    }

    /**
     *
     */
    private void startTimer() {
        if (mTimer != null) {
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLeftSeconds--;
                        if (mLeftSeconds <= 0) {
                            stopTimer();
                            checkTime();
                        } else {
                            formatTime();
                        }
                    }
                });
            }
        }, 2000L, 1000L);
    }

    /**
     *
     */
    private void stopTimer() {
        if (mTimer == null) {
            return;
        }
        mTimer.cancel();
        mTimer = null;
    }

    /**
     *
     */
    private void cashOut() {
        Intent intent = new Intent(this, WithdrawFishActivity.class);
        startActivityForResult(intent, REQ_WITHDRAW_FISH);
    }

    /**
     *
     */
    private void claim() {
        Intent intent = new Intent(this, WithdrawEthActivity.class);
        startActivityForResult(intent, REQ_WITHDRAW_ETH);
    }

    /**
     *
     */
    private void history() {
        Intent intent = new Intent(this, WithdrawHistoryActivity.class);
        startActivity(intent);
    }
}