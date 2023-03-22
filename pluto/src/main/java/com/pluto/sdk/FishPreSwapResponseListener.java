package com.pluto.sdk;

public interface FishPreSwapResponseListener {
    /**
     *
     * @param success
     * @param ethAmount
     * @param message
     */
    void onResponse(boolean success, double ethAmount, String message);
}
