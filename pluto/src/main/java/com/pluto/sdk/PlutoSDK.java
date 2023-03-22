package com.pluto.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pluto.config.BackgroundType;
import com.pluto.config.Config;

public class PlutoSDK {
    //
    private static final String TAG = "PlutoSDK";
    //
    @SuppressLint("StaticFieldLeak")
    private static PlutoSDK sInstance = null;
    //
    private boolean mIsInit = false;
    //
    private final CoreSDK mCoreSDK;

    /**
     * 设置SDK背景色，默认为BackgroundType.GREEN
     * @param type 对应3种背景颜色
     *             BackgroundType.GREEN
     *             BackgroundType.YELLow
     *             BackgroundType.RED
     */
    public static void setBackground(BackgroundType type) {
        CoreSDK.setBackgroundType(type);
    }

    /**
     *
     * @return
     */
    public static PlutoSDK share() {
        if (sInstance == null) {
            sInstance = new PlutoSDK();
        }
        return sInstance;
    }

    /**
     *
     */
    private PlutoSDK() {
        mCoreSDK = new CoreSDK();
    }

    /**
     * SDK初始化
     * @param activity Android activity
     * @param gameId 游戏ID
     * @param adUnitId 广告ID
     * @param isDebug 测试模式
     * @param listener 回调监听接口
     */
    public void initializeSdk(@NonNull Activity activity, String gameId, String adUnitId, boolean isDebug, PlutoSDKListener listener) {
        if (mIsInit) {
            Log.w(TAG, "PlutoSDK is initialized");
            return;
        }
        //
        Log.i(TAG, "========== PLUTO SDK INFO ==========\nInitializing Pluto SDK\nversion: " + Config.Version + "\n====================================");
        mIsInit = true;
        mCoreSDK.initialize(activity, gameId, adUnitId, isDebug, listener);
    }

    /**
     * 登录
     * @param activity Android activity
     */
    public void login(@NonNull Activity activity) {
        mCoreSDK.login(activity);
    }

    /**
     * 打开金币账户界面
     * @param activity Android activity
     */
    public void showCoinAccount(@NonNull Activity activity) {
        mCoreSDK.showCoinAccount(activity);
    }

    /**
     * 打开设置界面
     * @param activity Android activity
     */
    public void showSetting(@NonNull Activity activity) {
        mCoreSDK.showSetting(activity);
    }

    /**
     * 观看广告
     * @param activity Android activity
     */
    public void watchAD(@NonNull Activity activity) {
        mCoreSDK.watchAD(activity);
    }

    /**
     * 保存排行数据
     * @param score 排行积分
     */
    public void saveRankData(int score) {
        mCoreSDK.saveRankData(score);
    }

    /**
     * 获取排行详情
     * @param listener 回调监听
     */
    public void getRankInfo(RankResponseListener listener) {
        mCoreSDK.getRankInfo(listener);
    }
}
