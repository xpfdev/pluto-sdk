package com.pluto.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.pluto.sdk.CoreSDK;

public class PlutoBackground extends RelativeLayout {

    public PlutoBackground(Context context) {
        super(context);
        setBackgroundResource(CoreSDK.getBackgroundType().colorValue());
    }

    public PlutoBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(CoreSDK.getBackgroundType().colorValue());
    }

    public PlutoBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(CoreSDK.getBackgroundType().colorValue());
    }

    public PlutoBackground(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundResource(CoreSDK.getBackgroundType().colorValue());
    }
}
