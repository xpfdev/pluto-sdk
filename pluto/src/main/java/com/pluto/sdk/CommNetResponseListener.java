package com.pluto.sdk;

public interface CommNetResponseListener {
    /**
     *
     * @param success
     * @param message
     */
    void onResponse(boolean success, String message);
}
