package com.pluto.view;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pluto.config.PFType;
import com.pluto.sdk.CommNetResponseListener;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends PlutoActivity {
    //
    private static final String TAG = "LoginActivity";
    //
    private static final int RC_ONE_TAP = 1000;
    //
    private static final int REQ_EMAIL = 2000;
    //
    protected static final int RESULT_CODE_LOGIN_SUCCESS = 1;
    //
    protected static final int RESULT_CODE_LOGIN_FAILURE = 2;
    //
    private SignInClient mOneTapClient = null;
    //
    private BeginSignInRequest mSignInRequest = null;
    //
    private CallbackManager mCallbackManager = null;
    //
    private FirebaseAnalytics mFirebaseAnalytics = null;
    //
    private RelativeLayout mRlMore;
    //
    private Button mBtnEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_login_view_low : R.layout.pluto_login_view;
        setContentView(resId);
        //
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        //
        Button btnGoogle = findViewById(R.id.btn_login_google);
        btnGoogle.setOnClickListener(v -> {
            Log.i(TAG, "click google login...");
            loginGoogle();
        });
        //
        Button btnFacebook = findViewById(R.id.btn_login_facebook);
        btnFacebook.setOnClickListener(v -> {
            Log.i(TAG, "click facebook login...");
            loginFacebook();
        });
        //
        mBtnEmail = findViewById(R.id.btn_login_email);
        mBtnEmail.setOnClickListener(v -> {
            Log.i(TAG, "click email login...");
            loginEmail();
        });
        //
        mRlMore = findViewById(R.id.layout_more);
        //
        ImageView imgMore = findViewById(R.id.img_more);
        imgMore.setOnClickListener(v -> {
            mRlMore.setVisibility(View.GONE);
            mBtnEmail.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        //
        if (requestCode == RC_ONE_TAP) {
            try {
                SignInCredential credential = mOneTapClient.getSignInCredentialFromIntent(data);
                String userId = credential.getId();
                String token = credential.getGoogleIdToken();
                loginSuccess(PFType.GOOGLE, userId, token);
            } catch (ApiException e) {
                //
                loginErrorEvent(PFType.GOOGLE, "api error " + e.getStatusCode());
                //
                Log.i(TAG, "one tap get credential code==>" + e.getStatusCode());
                CoreSDK.getInstance().loginFailed(null);
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.DEVELOPER_ERROR:
                        Log.i(TAG, "getStatusCode==>" + "DEVELOPER_ERROR");
                        break;
                    case CommonStatusCodes.CANCELED:
                        Log.i(TAG, "getStatusCode==>" + "CANCELED");
                        break;
                }
            }
        }
        //
        if (requestCode == REQ_EMAIL && resultCode == RESULT_CODE_LOGIN_SUCCESS) {
            finish();
        }
    }

    /**
     *
     */
    private void loginGoogle() {
        //
        loginStartEvent(PFType.GOOGLE);
        //
        if (mOneTapClient == null) {
            mOneTapClient = Identity.getSignInClient(this);
        }
        //
        if (mSignInRequest == null) {
            String serverClientId = "";
            try {
                ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                serverClientId = appInfo.metaData.getString("com.pluto.google.server.clientId");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //
            if (serverClientId == null || serverClientId.equals("")) {
                //
                loginErrorEvent(PFType.GOOGLE, "clientId error");
                //
                Log.w(TAG, "Google server clientId is empty");
                CoreSDK.getInstance().loginFailed(null);
                return;
            }
            //
            mSignInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // Your server's client ID, not your Android client ID.
                            .setServerClientId(serverClientId)
                            // Show all accounts on the device.
                            .setFilterByAuthorizedAccounts(false)
                            .build())
                    .build();
        }
        //
        CoreSDK.showLoading(this, 90);
        mOneTapClient.beginSignIn(mSignInRequest).addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
            @Override
            public void onSuccess(BeginSignInResult beginSignInResult) {
                CoreSDK.hideLoading();
                try {
                    startIntentSenderForResult(beginSignInResult.getPendingIntent().getIntentSender(),
                            RC_ONE_TAP, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    //
                    loginErrorEvent(PFType.GOOGLE, "intent error");
                    //
                    Log.w(TAG, "google login error==>" + e);
                    CoreSDK.getInstance().loginFailed(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
                loginErrorEvent(PFType.GOOGLE, "listener error");
                //
                Log.w(TAG, "google login failure==>" + e);
                CoreSDK.hideLoading();
                CoreSDK.getInstance().loginFailed(null);
            }
        });
    }

    /**
     *
     */
    private void loginFacebook() {
        //
        loginStartEvent(PFType.FACEBOOK);
        //
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            String userId = accessToken.getUserId();
            String token = accessToken.getToken();
            loginSuccess(PFType.FACEBOOK, userId, token);
            return;
        }
        //
        if (mCallbackManager == null) {
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    String userId = loginResult.getAccessToken().getUserId();
                    String token = loginResult.getAccessToken().getToken();
                    loginSuccess(PFType.FACEBOOK, userId, token);
                }

                @Override
                public void onCancel() {
                    //
                    loginErrorEvent(PFType.FACEBOOK, "cancel error");
                    //
                    Log.i(TAG, "Facebook login cancel");
                    CoreSDK.getInstance().loginFailed(null);
                }

                @Override
                public void onError(@NonNull FacebookException e) {
                    //
                    loginErrorEvent(PFType.FACEBOOK, "inner error");
                    //
                    Log.i(TAG, "Facebook login error: " + e);
                    LoginManager.getInstance().logOut();
                    CoreSDK.getInstance().loginFailed(null);
                }
            });
        }
        //
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
    }

    /**
     *
     */
    private void loginEmail() {
        Intent intent = new Intent(this, EmailActivity.class);
        startActivityForResult(intent, REQ_EMAIL);
    }

    /**
     *
     * @param type
     * @param userId
     * @param token
     */
    private void loginSuccess(PFType type, String userId, String token) {
        //
        loginSdkEvent(type);
        //
        try {
            JSONObject obj = new JSONObject();
            obj.put("userid", userId);
            obj.put("token", token);
            CoreSDK.showLoading(this, 30);
            CoreSDK.getInstance().platformLogin(obj, type, new CommNetResponseListener() {
                @Override
                public void onResponse(boolean success, String message) {
                    CoreSDK.hideLoading();
                    if (LoginActivity.this.isDestroyed()) {
                        return;
                    }
                    //
                    if (success) {
                        loginFinishEvent(type);
                        //
                        finish();
                    } else {
                        if (message != null && !message.equals("")) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            CoreSDK.getInstance().loginFailed(null);
        }
    }

    /**
     * 登录开始事件
     * @param type 登录方式
     */
    private void loginStartEvent(PFType type) {
        Bundle bundle = new Bundle();
        bundle.putString("method", type.name());
        mFirebaseAnalytics.logEvent("p_login_platform_start", bundle);
    }

    /**
     * 登录SDK事件
     * @param type 登录方式
     */
    private void loginSdkEvent(PFType type) {
        Bundle bundle = new Bundle();
        bundle.putString("method", type.name());
        mFirebaseAnalytics.logEvent("p_login_platform_sdk", bundle);
    }

    /**
     * 登录完成事件
     * @param type 登录方式
     */
    private void loginFinishEvent(PFType type) {
        Bundle bundle = new Bundle();
        bundle.putString("method", type.name());
        mFirebaseAnalytics.logEvent("p_login_platform_finish", bundle);
    }

    /**
     * 登录异常事件
     * @param type 登录方式
     * @param param 异常
     */
    private void loginErrorEvent(PFType type, String param) {
        Bundle bundle = new Bundle();
        bundle.putString("method", type.name());
        bundle.putString("error", param);
        mFirebaseAnalytics.logEvent("p_login_platform_error", bundle);
    }
}
