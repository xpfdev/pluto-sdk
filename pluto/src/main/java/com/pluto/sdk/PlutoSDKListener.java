package com.pluto.sdk;

public interface PlutoSDKListener {
    /**
     * 初始化结果
     * @param idToken 验证账号使用的token
     * @param plutoUid 用户ID
     * @param coinAmount 代币数量
     */
    void onInitResult(String idToken, String plutoUid, long coinAmount);
    /**
     * 登录完成
     * @param idToken 验证账号使用的token
     * @param plutoUid 用户ID
     * @param coinAmount 代币数量
     */
    void onLoginCompleted(String idToken, String plutoUid, long coinAmount);

    /**
     * 登录失败
     */
    void onLoginFailed();

    /**
     * 登出
     */
    void onLogout();

    /**
     * 观看广告完成
     * @param allAmount 代币总数量
     * @param addAmount 观看广告增加的代币数量
     */
    void onAdPlayCompleted(long allAmount, int addAmount);

    /**
     * 观看广告失败
     */
    void onAdPlayFailed();

    /**
     * 刷新代币总量
     * @param amount 代币总量
     */
    void onUpdateCoinAmount(long amount);
}
