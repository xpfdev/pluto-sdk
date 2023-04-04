package com.pluto.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class PlutoActivity extends Activity {
    //
    private static final String TAG = "PlutoActivity";
    //
    private Unregistrar unregistrar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //刘海屏沉浸式状态栏和导航栏设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        //
        hideBarMenu();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //部分手机后台切回前台导航栏未隐藏
        if (hasFocus) {
            hideBarMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addKeyboardVisibilityEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeKeyboardVisibilityEvent();
    }

    /**
     * 软键盘显示隐藏状态
     * 子类有需求直接重写此方法
     * @param show
     */
    protected void onKeyboardVisibility(boolean show) {

    }

    /**
     * 隐藏状态栏和导航栏菜单
     */
    public void hideBarMenu() {
        try {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置点击EditText以外区域隐藏键盘
     * @param view
     */
    protected void setCloseKeyboard(View view) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        View currentFocus = PlutoActivity.this.getCurrentFocus();
                        if (currentFocus != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager)PlutoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setCloseKeyboard(innerView);
            }
        }
    }

    /**
     * 设置软键盘显示隐藏事件
     */
    private void addKeyboardVisibilityEvent() {
        unregistrar = KeyboardVisibilityEvent.registerEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                //解决部分手机软键盘隐藏导航栏未隐藏
                if (!isOpen) {
                    hideBarMenu();
                }
                onKeyboardVisibility(isOpen);
            }
        });
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

            }
        });
    }

    /**
     * 移除软键盘显示隐藏事件
     */
    private void removeKeyboardVisibilityEvent() {
        if (unregistrar != null) {
            unregistrar.unregister();
        }
    }
}
