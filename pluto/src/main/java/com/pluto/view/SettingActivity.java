package com.pluto.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pluto.config.Config;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

public class SettingActivity extends PlutoActivity {
    //
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_setting_view_low : R.layout.pluto_setting_view;
        setContentView(resId);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        //
        RelativeLayout layoutAccount = findViewById(R.id.layout_account);
        RelativeLayout layoutWallet = findViewById(R.id.layout_wallet);
        TextView txtAccount = findViewById(R.id.txt_account);
        TextView txtAmount = findViewById(R.id.txt_amount);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView txtLogout = findViewById(R.id.txt_log_out);
        //
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //
        layoutWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWalletForCustomTap();
            }
        });
        //
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        //
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        //
        if (!CoreSDK.getInstance().getIsLogin()) {
            layoutWallet.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            txtLogout.setVisibility(View.INVISIBLE);
            txtAccount.setText("no login");
            return;
        }
        //
        layoutWallet.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        txtLogout.setVisibility(View.VISIBLE);
        String email = CoreSDK.getInstance().getAccount().getEmail();
        if (email != null && !email.equals("")) {
            txtAccount.setText(email);
        } else {
            txtAccount.setText(CoreSDK.getInstance().getAccount().getPlutoUid());
        }
        double worth = CoreSDK.getInstance().getAccount().getFishBalanceWorth() + CoreSDK.getInstance().getAccount().getEthBalanceWorth();
        txtAmount.setText(ConvertUtils.double2Decimal(worth, 2, 2, true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开customTap失败
        if (requestCode == CustomTabActivity.CUSTOM_TAB_OPEN_REQUEST_CODE && resultCode == CustomTabActivity.CUSTOM_TAB_OPEN_ERROR_RESULT_CODE) {
            openWalletForWebView();
        }
    }

    /**
     *
     */
    private void openWalletForCustomTap() {
        String url = CoreSDK.getIsDebug() ? Config.DebugWalletUrl : Config.WalletUrl;
        url += "?token=" + CoreSDK.getInstance().getAccount().getToken() + "&plantformtype=android&hideback=1&t=" + System.currentTimeMillis();
        Log.i(TAG, "url==>" + url);
        Intent intent = new Intent(this, CustomTabActivity.class);
        intent.putExtra("url", url);
        intent.setAction(CustomTabActivity.CUSTOM_TAB_OPEN_ACTION);
        startActivityForResult(intent, CustomTabActivity.CUSTOM_TAB_OPEN_REQUEST_CODE);
    }

    /**
     *
     */
    private void openWalletForWebView() {
        Intent intent = new Intent(this, WalletActivity.class);
        startActivity(intent);
    }

    /**
     *
     */
    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     */
    private void logout() {
        CoreSDK.getInstance().logout();
        finish();
    }
}