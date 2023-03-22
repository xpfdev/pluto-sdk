package com.pluto.net;

public interface NetListener {
    void onNetResponse(boolean success, String result, String error);
}
