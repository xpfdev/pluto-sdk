package com.pluto.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.pluto.config.PFType;
import com.pluto.sdk.CoreSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Account {
    //
    private static final String TOKEN_KEY = "PLUTO_TOKEN_KEY";
    //
    private static final String LOGIN_TYPE_KEY = "LOGIN_TYPE_KEY";
    //登录类型
    private PFType mLoginType = PFType.NONE;
    //token
    private String mToken;
    //用于游戏方验证账号
    private String mIdToken;
    //用户唯一ID
    private String mPlutoUid;
    //用户Email，可能为空
    private String mEmail;
    //用户钱包地址
    private String mWalletAddress;
    //小鱼代币余额
    private long mFishBalance = 0;
    //小鱼代币余额兑美元价值
    private double mFishBalanceWorth = 0;
    //小鱼代币余额对ETH价值
    private double mFishBalance2Eth = 0;
    //小鱼代币最小提现数量
    private int mFishMiniWithdraw = 0;
    //小鱼代币下次提现时间
    private long mFishNextWithdrawTime = 0;
    //小鱼代币提现手续费
    private int mFishFee = 0;
    //ETH余额
    private double mEthBalance = 0;
    //ETH余额兑美元价值
    private double mEthBalanceWorth = 0;
    //ETH最小提现数量
    private double mEthMiniWithdraw = 0;
    //ETH提现手续费
    private double mEthFee = 0;
    //提现记录
    private List<History> mListHistory = new ArrayList<>();

    /**
     *
     */
    public Account() {
        Activity activity = CoreSDK.getRootActivity();
        if (activity != null) {
            SharedPreferences sp = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            mToken = sp.getString(TOKEN_KEY, null);
            mLoginType = PFType.formatType(sp.getInt(LOGIN_TYPE_KEY, 0));
        }
    }

    public PFType getLoginType() {
        return mLoginType;
    }

    public void setLoginType(PFType value) {
        this.mLoginType = value;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String value) {
        this.mToken = value;
    }

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String value) {
        this.mIdToken = value;
    }

    public String getPlutoUid() {
        return mPlutoUid;
    }

    public void setPlutoUid(String value) {
        this.mPlutoUid = value;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String value) {
        this.mEmail = value;
    }

    public String getWalletAddress() {
        return mWalletAddress;
    }

    public void setWalletAddress(String value) {
        this.mWalletAddress = value;
    }

    public long getFishBalance() {
        return mFishBalance;
    }

    public void setFishBalance(long value) {
        this.mFishBalance = value;
    }

    public double getFishBalanceWorth() {
        return mFishBalanceWorth;
    }

    public void setFishBalanceWorth(double value) {
        this.mFishBalanceWorth = value;
    }

    public double getFishBalance2Eth() {
        return mFishBalance2Eth;
    }

    public void setFishBalance2Eth(double value) {
        this.mFishBalance2Eth = value;
    }

    public int getFishMiniWithdraw() {
        return mFishMiniWithdraw;
    }

    public void setFishMiniWithdraw(int value) {
        this.mFishMiniWithdraw = value;
    }

    public long getFishNextWithdrawTime() {
        return mFishNextWithdrawTime;
    }

    public void setFishNextWithdrawTime(long value) {
        this.mFishNextWithdrawTime = value;
    }

    public int getFishFee() {
        return mFishFee;
    }

    public void setFishFee(int value) {
        this.mFishFee = value;
    }

    public double getEthBalance() {
        return mEthBalance;
    }

    public void setEthBalance(double value) {
        this.mEthBalance = value;
    }

    public double getEthBalanceWorth() {
        return mEthBalanceWorth;
    }

    public void setEthBalanceWorth(double value) {
        this.mEthBalanceWorth = value;
    }

    public double getEthMiniWithdraw() {
        return mEthMiniWithdraw;
    }

    public void setEthMiniWithdraw(double value) {
        this.mEthMiniWithdraw = value;
    }

    public double getEthFee() {
        return mEthFee;
    }

    public void setEthFee(double value) {
        this.mEthFee = value;
    }

    public List<History> getListHistory() {
        return mListHistory;
    }

    /**
     *
     */
    public void recoverData() {
        setIdToken(null);
        setPlutoUid(null);
        setToken(null);
        setEmail(null);
        setWalletAddress(null);
        setFishBalance(0);
        setFishBalanceWorth(0);
        setFishBalance2Eth(0);
        setFishMiniWithdraw(0);
        setFishNextWithdrawTime(0);
        setFishFee(0);
        setEthBalance(0);
        setEthBalanceWorth(0);
        setEthMiniWithdraw(0);
        setEthFee(0);
        mListHistory.clear();
        removeLocalToken();
    }

    /**
     *
     * @param data
     */
    public void parseData(@NonNull JSONObject data) {
        boolean isInit = getPlutoUid() == null;
        long oldFishBalance = getFishBalance();
        try {
            if (data.has("idtoken")) {
                setIdToken(data.getString("idtoken"));
            }
            if (data.has("plutouid")) {
                setPlutoUid(data.getString("plutouid"));
            }
            if (data.has("token")) {
                setToken(data.getString("token"));
            }
            if (data.has("email")) {
                setEmail(data.getString("email"));
            }
            if (data.has("address")) {
                setWalletAddress(data.getString("address"));
            }
            if (data.has("fishbalance")) {
                setFishBalance(data.getLong("fishbalance"));
            }
            if (data.has("fishbalanceworth")) {
                setFishBalanceWorth(data.getDouble("fishbalanceworth"));
            }
            if (data.has("fishbalance2eth")) {
                setFishBalance2Eth(data.getDouble("fishbalance2eth"));
            }
            if (data.has("fishminwithdraw")) {
                setFishMiniWithdraw(data.getInt("fishminwithdraw"));
            }
            if (data.has("fishnextwithdrawtime")) {
                setFishNextWithdrawTime(data.getLong("fishnextwithdrawtime") * 1000);
            }
            if (data.has("fishgasfee")) {
                setFishFee(data.getInt("fishgasfee"));
            }
            if (data.has("ethbalance")) {
                setEthBalance(data.getDouble("ethbalance"));
            }
            if (data.has("ethbalanceworth")) {
                setEthBalanceWorth(data.getDouble("ethbalanceworth"));
            }
            if (data.has("ethminwithdraw")) {
                setEthMiniWithdraw(data.getDouble("ethminwithdraw"));
            }
            if (data.has("ethgasfee")) {
                setEthFee(data.getDouble("ethgasfee"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        long newFishBalance = getFishBalance();
        if (!isInit && oldFishBalance != newFishBalance) {
            CoreSDK.getInstance().fishBalanceChanged();
        }
        //
        saveLocalToken();
    }

    /**
     *
     * @param array
     */
    public void parseHistory(@NonNull JSONArray array) {
        mListHistory.clear();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject item = (JSONObject) array.get(i);
                int source = item.getInt("source");
                if (source == 2 || source == 5) {
                    double fromAmount = item.getDouble("fromamount");
                    double toAmount = item.getDouble("toamount");
                    long time = item.getLong("time");
                    double fee = 0;
                    if (item.has("gasfee")) {
                        fee = item.getDouble("gasfee");
                    }
                    History history = new History();
                    history.setType(source);
                    history.setFromAmount(fromAmount);
                    history.setToAmount(toAmount);
                    history.setFee(fee);
                    history.setTime(time);
                    mListHistory.add(history);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void saveLocalToken() {
        Activity activity = CoreSDK.getRootActivity();
        if (activity == null || mToken == null) {
            return;
        }
        //
        SharedPreferences sp = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
        sp.edit().putString(TOKEN_KEY, mToken)
                .putInt(LOGIN_TYPE_KEY, mLoginType.numValue())
                .apply();
    }

    /**
     *
     */
    private void removeLocalToken() {
        Activity activity = CoreSDK.getRootActivity();
        if (activity == null) {
            return;
        }
        SharedPreferences sp = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
        sp.edit().remove(TOKEN_KEY)
                .remove(LOGIN_TYPE_KEY)
                .apply();
    }
}
