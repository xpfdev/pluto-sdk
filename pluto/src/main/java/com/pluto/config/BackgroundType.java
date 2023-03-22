package com.pluto.config;

import com.pluto.sdk.R;

public enum BackgroundType {
    GREEN(R.color.pluto_background_green),
    YELLOW(R.color.pluto_background_yellow),
    RED(R.color.pluto_background_red);
    //
    private int nv;

    /**
     *
     * @param v
     */
    BackgroundType(int v) {
        this.nv = v;
    }

    /**
     *
     * @return
     */
    public int colorValue() {
        return this.nv;
    }
}
