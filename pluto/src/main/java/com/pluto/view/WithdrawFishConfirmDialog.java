package com.pluto.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pluto.config.ClickType;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

public class WithdrawFishConfirmDialog extends PlutoDialog {
    //
    private static final String TAG = "WithdrawFishConfirmDialog";
    //
    private long mFishNum;
    //
    private double mEthNum;
    //
    private int mFee;
    //
    private String mAddress;

    /**
     *
     * @param context
     * @param listener
     * @param fishNum
     * @param ethNum
     * @param fee
     * @param address
     */
    public WithdrawFishConfirmDialog(@NonNull Context context, PlutoClickListener listener, long fishNum, double ethNum, int fee, String address) {
        super(context, listener);
        mFishNum = fishNum;
        mEthNum = ethNum;
        mFee = fee;
        mAddress = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_fish_confirm_dialog_low : R.layout.pluto_withdraw_fish_confirm_dialog;
        setContentView(resId);
        //
        TextView txtFishNum = findViewById(R.id.txt_fish_num);
        TextView txtEthNum = findViewById(R.id.txt_eth_num);
        TextView txtFee = findViewById(R.id.txt_fee);
        TextView txtAddress = findViewById(R.id.txt_address);
        txtFishNum.setText(ConvertUtils.num2Thousandth(mFishNum));
        txtEthNum.setText(ConvertUtils.double2Decimal(mEthNum, 4, 2, true));
        txtFee.setText("Fee: " + mFee + "FISH");
        txtAddress.setText(mAddress);
        //
        Button btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(ClickType.SURE);
                dismiss();
            }
        });
        //
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(ClickType.CANCEL);
                dismiss();
            }
        });
    }
}
