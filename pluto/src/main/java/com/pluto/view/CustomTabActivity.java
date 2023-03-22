package com.pluto.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

public class CustomTabActivity extends Activity {
    //
    private static final String TAG = "CustomTabActivity";
    //
    protected static final int CUSTOM_TAB_OPEN_REQUEST_CODE = 1;
    //
    protected static final int CUSTOM_TAB_OPEN_ERROR_RESULT_CODE = 1000;
    //
    protected static final String CUSTOM_TAB_OPEN_ACTION = "pluto.custom.tap.open_action";
    //
    protected static final String CUSTOM_TAB_CLOSE_ACTION = "pluto.custom.tap.close_action";
    //
    private boolean mShouldCloseCustomTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "CustomTabActivity onCreate");
        Intent intent = getIntent();
        //
        if (CUSTOM_TAB_CLOSE_ACTION.equals(intent.getAction())) {
            finish();
            return;
        }
        //
        if (intent.getAction().equals(CUSTOM_TAB_OPEN_ACTION)) {
            mShouldCloseCustomTab = false;
            String url = intent.getStringExtra("url");
            String packageName = CustomTabsHelper.getPackageNameToUse(this, url);
            if (packageName == null) {
                setResult(CUSTOM_TAB_OPEN_ERROR_RESULT_CODE, intent);
                finish();
            } else {
                CustomTabsIntent.Builder customTabsBuilder = new CustomTabsIntent.Builder();
                customTabsBuilder.setShowTitle(false);
                customTabsBuilder.setShareState(CustomTabsIntent.SHARE_STATE_OFF);
                CustomTabsIntent customTabsIntent = customTabsBuilder.build();
                customTabsIntent.intent.setPackage(packageName);
                customTabsIntent.launchUrl(this, Uri.parse(url));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "CustomTabActivity onNewIntent getAction==>" + intent.getAction());
        if (CUSTOM_TAB_CLOSE_ACTION.equals(intent.getAction())) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "CustomTabActivity onResume shouldCloseCustomTab==>" + mShouldCloseCustomTab);
        if (mShouldCloseCustomTab) {
            finish();
        }
        mShouldCloseCustomTab = true;
    }
}
