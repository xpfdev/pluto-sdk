package com.mmobay.plutodemo;

import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pluto.config.BackgroundType;
import com.pluto.entity.RankInfo;
import com.pluto.sdk.PlutoSDK;
import com.pluto.sdk.PlutoSDKListener;
import com.pluto.sdk.RankResponseListener;
import com.pluto.view.PlutoActivity;

import java.util.Arrays;

public class MainActivity extends PlutoActivity {

    private TextView txtUser;
    private TextView txtCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser = findViewById(R.id.txtUser);
        txtCoin = findViewById(R.id.txtCoin);
        //
        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlutoSDK.share().login(MainActivity.this);
            }
        });
        //
        Button btnWatch = findViewById(R.id.watchAD);
        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlutoSDK.share().watchAD(MainActivity.this);
            }
        });
        //
        Button btnCoinAccount = findViewById(R.id.coinAccount);
        btnCoinAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlutoSDK.share().showCoinAccount(MainActivity.this);
            }
        });
        //
        Button btnSetting = findViewById(R.id.setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlutoSDK.share().showSetting(MainActivity.this);
            }
        });
        //
        RadioButton rbGreen = findViewById(R.id.rbGreen);
        RadioButton rbYellow = findViewById(R.id.rbYellow);
        RadioButton rbRed = findViewById(R.id.rbRed);
        RadioGroup rgColor = findViewById(R.id.rgColor);
        rgColor.check(R.id.rbGreen);
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("DEMO", "choose color==>" + checkedId);
                if (checkedId == rbGreen.getId()) {
                    PlutoSDK.setBackground(BackgroundType.GREEN);
                } else if (checkedId == rbYellow.getId()) {
                    PlutoSDK.setBackground(BackgroundType.YELLOW);
                } else if (checkedId == rbRed.getId()) {
                    PlutoSDK.setBackground(BackgroundType.RED);
                }
            }
        });
        //
        LoadingBar.open(this, 30);
        PlutoSDK.share().initializeSdk(this, getString(R.string.game_id), getString(R.string.ad_unit_id), true, new PlutoSDKListener() {
            @Override
            public void onInitResult(String idToken, String plutoUid, long coinAmount) {
                LoadingBar.close();
                Log.i("DEMO", "init plutoUid==>" + plutoUid);
                Log.i("DEMO", "init coinAmount==>" + coinAmount);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (plutoUid != null) {
                            txtUser.setText(plutoUid);
                            txtCoin.setText(coinAmount + "");
                            btnLogin.setVisibility(View.INVISIBLE);
                            btnCoinAccount.setVisibility(View.VISIBLE);
                        } else {
                            txtUser.setText("Guest");
                            txtCoin.setText("0");
                            btnLogin.setVisibility(View.VISIBLE);
                            btnCoinAccount.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onLoginCompleted(String idToken, String plutoUid, long coinAmount) {
                Log.i("DEMO", "login plutoUid==>" + plutoUid);
                Log.i("DEMO", "login coinAmount==>" + coinAmount);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (plutoUid != null) {
                            txtUser.setText(plutoUid);
                            txtCoin.setText(coinAmount + "");
                            btnLogin.setVisibility(View.INVISIBLE);
                            btnCoinAccount.setVisibility(View.VISIBLE);
                        } else {
                            txtUser.setText("Guest");
                            txtCoin.setText("0");
                            btnLogin.setVisibility(View.VISIBLE);
                            btnCoinAccount.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onLoginFailed() {
                Log.i("DEMO", "login failed");
            }

            @Override
            public void onLogout() {
                Log.i("DEMO", "logout");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtUser.setText("Guest");
                        txtCoin.setText("0");
                        btnLogin.setVisibility(View.VISIBLE);
                        btnCoinAccount.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onAdPlayCompleted(long allAmount, int addAmount) {
                Log.i("DEMO", "play ad completed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCoin.setText(allAmount + "");
                        Toast.makeText(MainActivity.this, "Add coin " + addAmount, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onAdPlayFailed() {
                Log.i("DEMO", "play ad failed");
            }

            @Override
            public void onUpdateCoinAmount(long amount) {
                Log.i("DEMO", "update coin amount");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCoin.setText(amount + "");
                    }
                });
            }
        });
    }
}