package com.pluto.sdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;

public class PlutoAD implements MaxRewardedAdListener {
    //
    private static final String TAG = "PlutoAD";
    //
    private static final Handler sHandle = new Handler(Looper.getMainLooper());
    //
    private boolean mIsInit;
    //
    private String mAdId;
    //
    private MaxRewardedAd mRewardedAd;
    //
    private boolean mAutoShow;
    //
    private int mRetryAttempt = 0;
    //
    private boolean mNeedLoading = false;
    /**
     *
     * @param adId 广告ID
     */
    protected PlutoAD(String adId) {
        Activity activity = CoreSDK.getRootActivity();
        if (activity == null) {
            Log.w(TAG, "No context specified");
            return;
        }
        AppLovinSdk instance = AppLovinSdk.getInstance(activity);
        instance.setMediationProvider(AppLovinMediationProvider.MAX);
        instance.initializeSdk(config -> {
            mIsInit = true;
            mAdId = adId;
            mRewardedAd = MaxRewardedAd.getInstance(adId, activity);
            mRewardedAd.setListener(this);
            //预加载广告
            mRewardedAd.loadAd();
        });
        //默认设置999999
        instance.setUserIdentifier("999999");
    }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    protected void setUserId(String userId) {
        Activity activity = CoreSDK.getRootActivity();
        if (activity == null) {
            Log.w(TAG, "No context specified");
            return;
        }
        //
        AppLovinSdk.getInstance(activity).setUserIdentifier(userId);
    }

    /**
     *
     */
    protected void watchAD() {
        if (!mIsInit) {
            Log.w(TAG, "AD SDK not initialize");
            return;
        }
        //
        Activity activity = CoreSDK.getRootActivity();
        if (activity == null) {
            Log.w(TAG, "No context specified");
            return;
        }
        //
        if (mRewardedAd.isReady()) {
            String data = CoreSDK.getInstance().getGameId() + "_" + (CoreSDK.getIsDebug() ? 0 : 1);
            mRewardedAd.showAd("GameScreen", data);
            return;
        }
        //
        mAutoShow = true;
        mRewardedAd.loadAd();
        mNeedLoading = true;
        CoreSDK.showLoading(activity, 30);
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        Log.i(TAG, "onAdLoaded");
        mRetryAttempt = 0;
        if (mNeedLoading) {
            CoreSDK.hideLoading();
            mNeedLoading = false;
        }
        //加载完自动播放
        if (mAutoShow) {
            String data = CoreSDK.getInstance().getGameId() + "_" + (CoreSDK.getIsDebug() ? 0 : 1);
            mRewardedAd.showAd("GameScreen", data);
        }
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.i(TAG, "Retry load:" + mRetryAttempt);
        //广告加载失败1000ms后重试，重试5次
        mRetryAttempt++;
        if (mRetryAttempt > 5) {
            Log.w(TAG, "AD load failure");
            mRetryAttempt = 0;
            if (mNeedLoading) {
                CoreSDK.hideLoading();
                mNeedLoading = false;
            }
            CoreSDK.getInstance().adPlayFailed();
            return;
        }
        //
        sHandle.postDelayed(() -> {
            mRewardedAd.loadAd();
        }, 1000);
    }

    @Override
    public void onAdDisplayed(MaxAd maxAd) {
        Log.i(TAG, "onAdDisplayed");
    }

    @Override
    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
        Log.i(TAG, "onAdDisplayFailed");
        if (mAutoShow) {
            Activity activity = CoreSDK.getRootActivity();
            if (activity != null) {
                mNeedLoading = true;
                CoreSDK.showLoading(activity, 30);
            }
        }
        //广告展示失败重试
        mRewardedAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(MaxAd maxAd) {
        Log.i(TAG, "onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted(MaxAd maxAd) {
        Log.i(TAG, "onRewardedVideoCompleted");
    }

    @Override
    public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
        Log.i(TAG, "onUserRewarded");
        CoreSDK.getInstance().adPlayCompleted(maxReward.getAmount());
    }

    @Override
    public void onAdHidden(MaxAd maxAd) {
        Log.i(TAG, "onAdHidden");
        //广告播放完，预加载下个广告
        mAutoShow = false;
        mRewardedAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd maxAd) {
        Log.i(TAG, "onAdClicked");
    }
}
