package com.pluto.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pluto.config.PFType;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.sdk.CommNetResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class EmailActivity extends PlutoActivity {
    //
    private static final String TAG = "PlutoEmailActivity";
    //
    private RelativeLayout mLayoutSend;
    //
    private TextView mTxtSend;
    //
    private EditText mTxtEmail;
    //
    private EditText mTxtCode;
    //
    private String mEmail = null;
    //
    private String mCode = null;
    //
    private int mSeconds = 120;
    //
    private CountDownTimer mTimer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_email_view_low : R.layout.pluto_email_view;
        setContentView(resId);
        // 设置点击输入框以外区域关闭键盘
        RelativeLayout layoutContent = findViewById(R.id.layout_main_content);
        setCloseKeyboard(layoutContent);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            back(LoginActivity.RESULT_CODE_LOGIN_FAILURE);
        });
        //
        ImageButton btnLoginEmail = findViewById(R.id.btn_login_email);
        btnLoginEmail.setOnClickListener(v -> {
            Log.i(TAG, "click login email");
            loginEmail();
        });
        //
        mLayoutSend = findViewById(R.id.layout_send);
        mLayoutSend.setOnClickListener(v -> {
            Log.i(TAG, "click send code");
            sendCode();
        });
        mTxtSend = findViewById(R.id.txt_send);
        //
        mTxtEmail = findViewById(R.id.txt_email);
        mTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        mTxtCode = findViewById(R.id.txt_code);
        mTxtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCode = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    @Override
    protected void onKeyboardVisibility(boolean show) {
        super.onKeyboardVisibility(show);
        if (!show) {
            mTxtEmail.clearFocus();
            mTxtCode.clearFocus();
        }
    }

    /**
     *
     */
    private void back(int resultCode) {
        setResult(resultCode);
        finish();
    }

    /**
     * 获取验证码
     */
    private void sendCode() {
        if (!checkEmailValid(mEmail)) {
            Toast.makeText(this, "Email address is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        CoreSDK.getInstance().getVerifyCode(mEmail, new CommNetResponseListener() {
            @Override
            public void onResponse(boolean success, String message) {
                if (EmailActivity.this.isDestroyed()) {
                    return;
                }
                //
                if (success) {
                    Toast.makeText(EmailActivity.this, "Verify code send success", Toast.LENGTH_SHORT).show();
                    startCountDown();
                } else {
                    if (message != null && !message.equals("")) {
                        Toast.makeText(EmailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 邮箱验证码登录
     */
    private void loginEmail() {
        if (!checkCodeValid(mCode)) {
            Toast.makeText(this, "Verification code is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", mEmail);
            obj.put("code", mCode);
            CoreSDK.getInstance().platformLogin(obj, PFType.EMAIL, new CommNetResponseListener() {
                @Override
                public void onResponse(boolean success, String message) {
                    if (EmailActivity.this.isDestroyed()) {
                        return;
                    }
                    if (success) {
                        back(LoginActivity.RESULT_CODE_LOGIN_SUCCESS);
                    } else {
                        if (message != null && !message.equals("")) {
                            Toast.makeText(EmailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void startCountDown() {
        //
        cancelTimer();
        //
        mLayoutSend.setEnabled(false);
        mTxtSend.setText(mSeconds + "s");
        //
        mTimer = new CountDownTimer(mSeconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSeconds--;
                mTxtSend.setText(mSeconds + "s");
            }

            @Override
            public void onFinish() {
                mLayoutSend.setEnabled(true);
                mTxtSend.setText("Resend code");
            }
        };
        mTimer.start();
    }

    /**
     *
     */
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        //
        mSeconds = 120;
    }

    /**
     * 验证游戏格式是否合法
     * @param email
     * @return
     */
    private boolean checkEmailValid(String email) {
        if (email == null || email.equals("")) {
            return false;
        }
        //
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+[\\.]{0,1}[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]+");
        return pattern.matcher(email).matches();
    }

    /**
     * 验证验证码是否合法
     * @param code
     * @return
     */
    private boolean checkCodeValid(String code) {
        if (code == null || code.equals("")) {
            return false;
        }
        //
        Pattern pattern = Pattern.compile("\\d{6}");
        return pattern.matcher(code).matches();
    }
}
