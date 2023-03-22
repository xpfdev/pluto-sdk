package com.pluto.entity;

public class History {
    //
    private int mType = 5;
    //
    private double mFromAmount = 0;
    //
    private double mToAmount = 0;
    //
    private double mFee = 0;
    //
    private long mTime = 0;

    /**
     *
     * @return
     */
    public int getType() {
        return mType;
    }

    /**
     *
     * @param value
     */
    public void setType(int value) {
        mType = value;
    }

    /**
     *
     * @return
     */
    public double getFromAmount() {
        return mFromAmount;
    }

    /**
     *
     * @param value
     */
    public void setFromAmount(double value) {
        mFromAmount = value;
    }

    /**
     *
     * @return
     */
    public double getToAmount() {
        return mToAmount;
    }

    /**
     *
     * @param value
     */
    public void setToAmount(double value) {
        mToAmount = value;
    }

    /**
     *
     * @return
     */
    public double getFee() {
        return mFee;
    }

    /**
     *
     * @param value
     */
    public void setFee(double value) {
        mFee = value;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return mTime;
    }

    /**
     *
     * @param value
     */
    public void setTime(long value) {
        mTime = value;
    }
}
