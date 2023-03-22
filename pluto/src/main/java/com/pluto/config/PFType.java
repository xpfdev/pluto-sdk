package com.pluto.config;

public enum PFType {
    NONE(0), EMAIL(1), GOOGLE(2), APPLE(3), FACEBOOK(4);
    //
    private int nv;

    /**
     *
     * @param num
     * @return
     */
    public static PFType formatType(int num) {
        if (num == EMAIL.numValue()) {
            return EMAIL;
        }
        if (num == GOOGLE.numValue()) {
            return GOOGLE;
        }
        if (num == APPLE.numValue()) {
            return APPLE;
        }
        if (num == FACEBOOK.numValue()) {
            return FACEBOOK;
        }
        return NONE;
    }

    /**
     *
     * @param v
     */
    PFType(int v) {
        this.nv = v;
    }

    /**
     *
     * @return
     */
    public int numValue() {
        return this.nv;
    }
}
