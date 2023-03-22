package com.pluto.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.pluto.sdk.R;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingDialog extends PlutoDialog {
    //
    private static final String TAG = "LoadingDialog";
    //
    private static Dialog mLoadingDialog = null;
    //
    private static Timer mTimer = null;

    /**
     *
     * @param context 上下文
     * @param durations 显示时间（秒），如果为-1一直显示，直到手动关闭
     */
    public static void open(Context context, long durations) {
        if (mLoadingDialog != null) {
            return;
        }
        //
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.show();
        if (durations != -1) {
            startTimer(durations);
        }
    }

    /**
     *
     */
    public static void close() {
        stopTimer();
        //
        if (mLoadingDialog == null) {
            return;
        }
        //
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    /**
     *
     * @param times
     */
    private static void startTimer(long times) {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
            }
        }, times * 1000);
    }

    /**
     *
     */
    private static void stopTimer() {
        if (mTimer == null) {
            return;
        }
        //
        mTimer.cancel();
        mTimer = null;
    }

    /**
     *
     * @param context
     */
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pluto_loading_dialog);
    }
}
