package com.pluto.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.pluto.config.BackgroundType;
import com.pluto.config.Config;
import com.pluto.config.PFType;
import com.pluto.entity.Account;
import com.pluto.entity.RankInfo;
import com.pluto.net.NetListener;
import com.pluto.net.NetUtil;
import com.pluto.view.CoinAccountActivity;
import com.pluto.view.LoadingDialog;
import com.pluto.view.LoginActivity;
import com.pluto.view.SettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CoreSDK {
    //
    private static final String TAG = "CoreSDK";
    //
    private static final Handler sHandle = new Handler(Looper.getMainLooper());
    //
    @SuppressLint("StaticFieldLeak")
    private static Activity sRootActivity = null;
    //
    private static BackgroundType sBackgroundType = BackgroundType.GREEN;
    //
    private static boolean sLowScreen = false;
    //
    private static boolean sIsDebug = false;
    //
    @SuppressLint("StaticFieldLeak")
    private static CoreSDK sInstance = null;
    //
    private String mGameId;
    //
    private boolean mIsLogin;
    //
    private PlutoSDKListener mSDKListener;
    //
    private PlutoAD mPlutoAD;
    //
    private Account mAccount;
    //
    private Timer mTimer;

    /**
     * 检测适配
     * @param context Android上下文
     */
    private static void checkScreenAdapt(Context context) {
        if (context == null) {
            Log.w(TAG, "No context specified");
            return;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        int widthPixels = point.x;
        int heightPixels = point.y;
        float widthDp = widthPixels / density;
        float heightDp = heightPixels / density;
        float ratio = heightDp / widthDp;
        Log.i(TAG, "displayWidth: " + widthPixels);
        Log.i(TAG, "displayHeight: " + heightPixels);
        Log.i(TAG, "widthDp: " + widthDp);
        Log.i(TAG, "heightDp: " + heightDp);
        Log.i(TAG, "ratio: " + ratio);
        if (heightDp < 800) {
            sLowScreen = true;
        }
    }

    /**
     * 获取单例
     * @return 返回CoreSDK唯一实例对象
     */
    public static CoreSDK getInstance() {
        return sInstance;
    }

    /**
     * 显示加载动画
     * @param activity Android activity
     * @param durations 显示时间
     */
    public static void showLoading(Activity activity, int durations) {
        if (activity == null || activity.isDestroyed()) {
            return;
        }
        //
        LoadingDialog.open(activity, durations);
    }

    /**
     * 隐藏加载动画
     */
    public static void hideLoading() {
        LoadingDialog.close();
    }

    /**
     * 获取背景颜色
     * @return 返回全局背景颜色
     */
    public static BackgroundType getBackgroundType() {
        return sBackgroundType;
    }

    /**
     * 设置背景颜色
     * @param type 背景颜色类型
     */
    public static void setBackgroundType(BackgroundType type) {
        if (type != null) {
            sBackgroundType = type;
        }
    }

    /**
     * 获取当前根节点activity
     * @return 返回根节点activity，根节点不可用返回null
     */
    public static Activity getRootActivity() {
        if (sRootActivity == null || sRootActivity.isDestroyed()) {
            sRootActivity = null;
            return null;
        }
        return sRootActivity;
    }

    /**
     * 获取是否为小屏适配
     * @return 返回小屏幕适配
     */
    public static boolean getLowScreen() {
        return sLowScreen;
    }

    /**
     * 获取是否为测试模式
     * @return 返回测试模式
     */
    public static boolean getIsDebug() {
        return sIsDebug;
    }

    /**
     *
     */
    public CoreSDK() {
        sInstance = this;
    }

    /**
     * 获取游戏ID
     * @return 返回游戏ID
     */
    public String getGameId() {
        return mGameId;
    }

    /**
     * 获取SDK是否登录
     * @return 返回是否登录
     */
    public boolean getIsLogin() {
        return mIsLogin;
    }

    /**
     * 获取Account实例
     * @return 返回Account实例
     */
    public Account getAccount() {
        return mAccount;
    }

    /**
     * 初始化
     * @param activity Android activity
     * @param gameId 游戏ID
     * @param adUnitId 广告ID
     * @param isDebug 是否测试模式
     * @param listener 回调监听
     */
    public void initialize(Activity activity, String gameId, String adUnitId, boolean isDebug, PlutoSDKListener listener) {
        checkScreenAdapt(activity);
        sRootActivity = activity;
        sIsDebug = isDebug;
        mGameId = gameId;
        mSDKListener = listener;
        mPlutoAD = new PlutoAD(adUnitId);
        mAccount = new Account();
        //
        silentLogin(new SilentLoginListener() {
            @Override
            public void onResponse() {
                String plutoUid = mAccount.getPlutoUid();
                if (plutoUid != null) {
                    mIsLogin = true;
                    mPlutoAD.setUserId(plutoUid);
                }
                //
                if (mSDKListener != null) {
                    mSDKListener.onInitResult(mAccount.getIdToken(), plutoUid, mAccount.getFishBalance());
                }
            }
        });
    }

    /**
     * 静默登录
     * @param listener 回调监听
     */
    public void silentLogin(SilentLoginListener listener) {
        Activity act = getRootActivity();
        if (act == null) {
            listener.onResponse();
            return;
        }
        //
        if (mAccount.getToken() == null) {
            listener.onResponse();
            return;
        }
        //
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.LoginWithTokenPath;
        NetUtil.get(urlPath, mAccount.getToken(), null, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    if (!success) {
                        listener.onResponse();
                        return;
                    }
                    //
                    JSONObject jsonData = null;
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.getInt("code");
                        if (code == 0) {
                            jsonData = obj.getJSONObject("data");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                    if (jsonData == null) {
                        mAccount.recoverData();
                    } else {
                        mAccount.parseData(jsonData);
                    }
                    listener.onResponse();
                });
            }
        });
    }

    /**
     * 平台登录
     * @param data 登录数据
     * @param type 登录类型
     * @param listener 回调监听
     */
    public void platformLogin(JSONObject data, PFType type, CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        Map<String, Object> params = new HashMap<>();
        params.put("gameid", mGameId);
        params.put("pid", type.numValue());
        if (type == PFType.EMAIL) {
            urlPath += Config.LoginWithEmailPath;
            try {
                params.put("email", data.getString("email"));
                params.put("seccode", data.getString("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            urlPath += Config.LoginWithPlatformPath;
            params.put("sdkdata", data);
        }
        //
        NetUtil.post(urlPath, null, params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    JSONObject jsonData = null;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            if (code == 0) {
                                jsonData = obj.getJSONObject("data");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    if (jsonData == null) {
                        loginFailed(message);
                        listener.onResponse(false, null);
                    } else {
                        mAccount.setLoginType(type);
                        mAccount.parseData(jsonData);
                        loginCompleted();
                        listener.onResponse(true, message);
                    }
                });
            }
        });
    }

    /**
     * 登录
     * @param activity Android activity
     */
    public void login(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "No activity specified");
            return;
        }
        sRootActivity = activity;
        //
        if (mIsLogin) {
            Toast.makeText(activity, "Account logged", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 登出
     */
    public void logout() {
        mIsLogin = false;
        mPlutoAD.setUserId("999999");
        mAccount.recoverData();
        //
        if (mSDKListener != null) {
            mSDKListener.onLogout();
        }
    }

    /**
     * 显示代币账号界面
     * @param activity Android activity
     */
    public void showCoinAccount(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "No activity specified");
            return;
        }
        sRootActivity = activity;
        //
        if (!mIsLogin) {
            login(activity);
            return;
        }
        //
        Intent intent = new Intent(activity, CoinAccountActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 显示设置界面
     * @param activity Android activity
     */
    public void showSetting(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "No activity specified");
            return;
        }
        sRootActivity = activity;
        //
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 观看广告
     * @param activity Android activity
     */
    public void watchAD(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "No activity specified");
            return;
        }
        sRootActivity = activity;
        //
        mPlutoAD.watchAD();
    }

    /**
     * 获取邮件验证码
     * @param email 邮箱地址
     * @param listener 回调监听
     */
    public void getVerifyCode(String email, CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.ReqCodePath;
        Map<String, Object> params = new HashMap<>();
        params.put("gameid", mGameId);
        params.put("email", email);
        NetUtil.post(urlPath, null, params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    boolean flag = false;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            flag = code == 0;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    listener.onResponse(flag, message);
                });
            }
        });
    }

    /**
     * 获取钱包信息
     * @param listener 回调监听
     */
    public void getWalletInfo(CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.ReqWalletInfoPath;
        NetUtil.get(urlPath, mAccount.getToken(), null, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                Log.i(TAG, "wallet info result==>" + result);
                sHandle.post(() -> {
                    JSONObject jsonData = null;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            if (code == 0) {
                                jsonData = obj.getJSONObject("data");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (jsonData == null) {
                        listener.onResponse(false, message);
                    } else {
                        mAccount.parseData(jsonData);
                        listener.onResponse(true, message);
                    }
                });
            }
        });
    }

    /**
     * 获取代币兑换信息
     * @param amount 兑换数量
     * @param listener 回调监听
     */
    public void getFishPreSwapInfo(long amount, FishPreSwapResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.PreFishSwapEthPath;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    boolean flag = false;
                    double ethAmount = 0;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            int code = obj.getInt("code");
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            flag = code == 0;
                            JSONObject data = obj.getJSONObject("data");
                            ethAmount = data.getDouble("preswapamount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listener.onResponse(flag, ethAmount, message);
                });
            }
        });
    }

    /**
     * 代币提现
     * @param fishAmount 提现数量
     * @param ethAmount 兑换ETH的数量
     * @param type 提现类型
     * @param walletAddress 钱包地址
     * @param listener 回调监听
     */
    public void fishWithdraw(long fishAmount, double ethAmount, int type, String walletAddress, CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.FishSwapEthPath;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", fishAmount);
        params.put("preswapamount", ethAmount);
        params.put("to", type);
        params.put("address", walletAddress);
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    boolean flag = false;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            flag = code == 0;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    listener.onResponse(flag, message);
                });
            }
        });
    }

    /**
     * ETH提现
     * @param amount 提现数量
     * @param walletAddress 钱包地址
     * @param listener 回调监听
     */
    public void ethWithdraw(double amount, String walletAddress, CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.EthWithdrawPath;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("address", walletAddress);
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    boolean flag = false;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            flag = code == 0;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    listener.onResponse(flag, message);
                });
            }
        });
    }

    /**
     * 获取提现历史记录
     * @param listener 回调监听
     */
    public void getWithdrawHistory(CommNetResponseListener listener) {
        String urlPath = sIsDebug ? Config.DebugAuthUrl : Config.AuthUrl;
        urlPath += Config.WithdrawLogPath;
        Map<String, Object> params = new HashMap<>();
        params.put("page", 0);
        params.put("pageSize", 100);
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    boolean flag = false;
                    String message = error;
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.has("message")) {
                                message = obj.getString("message");
                            }
                            int code = obj.getInt("code");
                            flag = code == 0;
                            JSONObject data = obj.getJSONObject("data");
                            mAccount.parseHistory(data.getJSONArray("list"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    listener.onResponse(flag, message);
                });
            }
        });
    }

    /**
     * 保存排行数据
     * @param score 排行分数
     */
    public void saveRankData(int score) {
        if (!mIsLogin) {
            return;
        }
        //
        String urlPath = sIsDebug ? Config.DebugRankUrl : Config.RankUrl;
        urlPath += Config.SaveScoreRankPath;
        Map<String, Object> params = new HashMap<>();
        params.put("score", score);
        params.put("name", "RANK");
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    Log.i(TAG, "save rank result==>" + result);
                });
            }
        });
    }

    /**
     * 获取排行数据
     * @param listener 回调监听
     */
    public void getRankInfo(RankResponseListener listener) {
        if (!mIsLogin) {
            if (listener != null) {
                listener.onResponse(new RankInfo());
            }
            return;
        }
        //
        String urlPath = sIsDebug ? Config.DebugRankUrl : Config.RankUrl;
        urlPath += Config.ReqScoreRankPath;
        Map<String, Object> params = new HashMap<>();
        params.put("name", "RANK");
        params.put("page", 0);
        params.put("pageSize", 100);
        NetUtil.post(urlPath, mAccount.getToken(), params, new NetListener() {
            @Override
            public void onNetResponse(boolean success, String result, String error) {
                sHandle.post(() -> {
                    Log.i(TAG, "get rank info result==>" + result);
                    RankInfo rankInfo = new RankInfo();
                    if (success) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            int code = obj.getInt("code");
                            if (code == 0) {
                                JSONObject data = obj.getJSONObject("data");
                                int rank = data.getInt("myrank");
                                int score = data.getInt("myscore");
                                JSONArray list = data.getJSONArray("list");
                                rankInfo.parseData(mAccount.getWalletAddress(), rank, score, list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //
                    if (listener != null) {
                        listener.onResponse(rankInfo);
                    }
                });
            }
        });
    }

    /**
     * 登录完成
     */
    public void loginCompleted() {
        mIsLogin = true;
        mPlutoAD.setUserId(mAccount.getPlutoUid());
        //
        if (mSDKListener != null) {
            mSDKListener.onLoginCompleted(mAccount.getIdToken(), mAccount.getPlutoUid(), mAccount.getFishBalance());
        }
        //
        Activity activity = getRootActivity();
        if (activity != null) {
            Toast.makeText(activity, "Login success", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登录失败
     * @param message 提示信息
     */
    public void loginFailed(String message) {
        if (message == null) {
            message = "Login failure";
        }
        if (mSDKListener != null) {
            mSDKListener.onLoginFailed();
        }
        //
        Activity activity = getRootActivity();
        if (activity != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 广告播放完成
     * @param addAmount 增加代币数量
     */
    public void adPlayCompleted(int addAmount) {
        long allAmount = 0;
        if (mIsLogin) {
            allAmount = mAccount.getFishBalance() + addAmount;
            //
            startGetWalletInfoTask();
        } else {
            addAmount = 0;
        }
        //
        if (mSDKListener != null) {
            mSDKListener.onAdPlayCompleted(allAmount, addAmount);
        }
    }

    /**
     * 广告播放失败
     */
    public void adPlayFailed() {
        if (mSDKListener != null) {
            mSDKListener.onAdPlayFailed();
        }
    }

    /**
     * 代币余额变化
     */
    public void fishBalanceChanged() {
        if (mSDKListener != null) {
            mSDKListener.onUpdateCoinAmount(mAccount.getFishBalance());
        }
    }

    /**
     * 开启一个获取钱包信息的任务
     * 在观看广告完成后，开启一个5s后执行的任务获取最新的钱包数据
     */
    private void startGetWalletInfoTask() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimer = null;
                getWalletInfo(new CommNetResponseListener() {
                    @Override
                    public void onResponse(boolean success, String message) {

                    }
                });
            }
        }, 5 * 1000L);
    }
}
