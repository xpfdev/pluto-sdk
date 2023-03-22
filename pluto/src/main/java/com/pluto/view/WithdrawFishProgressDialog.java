package com.pluto.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.pluto.config.ClickType;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;

import java.util.Timer;
import java.util.TimerTask;

public class WithdrawFishProgressDialog extends PlutoDialog {
    //
    private static final String TAG = "WithdrawFishProgressDialog";
    //
    private static final Handler sHandle = new Handler(Looper.getMainLooper());
    //
    private Button mBtnConfirm;
    //
    private Timer mTimer = null;
    //
    private int mSeconds = 6;
    /**
     *
     * @param context
     * @param listener
     */
    public WithdrawFishProgressDialog(@NonNull Context context, PlutoClickListener listener) {
        super(context, listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_fish_progress_dialog_low : R.layout.pluto_withdraw_fish_progress_dialog;
        setContentView(resId);
        //
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setEnabled(false);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(ClickType.SURE);
                dismiss();
            }
        });
        mBtnConfirm.setText("OK (" + mSeconds +"s)");
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sHandle.post(() -> {
                    mSeconds--;
                    if (mSeconds <= 0) {
                        mBtnConfirm.setEnabled(true);
                        mBtnConfirm.setText("OK");
                        stopTimer();
                    } else {
                        mBtnConfirm.setText("OK (" + mSeconds +"s)");
                    }
                });
            }
        }, 1000L, 1500L);
    }

    /**
     *
     */
    private void stopTimer() {
        if (mTimer == null) {
            return;
        }
        //
        mTimer.cancel();
        mTimer = null;
    }
}
