package com.pluto.sdk;

import com.pluto.entity.RankInfo;

public interface RankResponseListener {
    /**
     *
     * @param info
     */
    void onResponse(RankInfo info);
}
