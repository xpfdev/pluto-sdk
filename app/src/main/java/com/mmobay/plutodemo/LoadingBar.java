package com.mmobay.plutodemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingBar extends Dialog {
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
        mLoadingDialog = new LoadingBar(context);
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
    public LoadingBar(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        //是否可以按“返回键”消失
        setCancelable(false);
        //点击加载框以外的区域
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setAttributes(lp);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

}
