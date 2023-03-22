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

public class WithdrawEthConfirmDialog extends PlutoDialog{
    //
    private static final String TAG = "WithdrawFishConfirmDialog";
    //
    private double mAmount;
    //
    private double mFee;
    //
    private String mAddress;

    /**
     *
     * @param context
     * @param listener
     * @param amount
     * @param fee
     * @param address
     */
    public WithdrawEthConfirmDialog(@NonNull Context context, PlutoClickListener listener, double amount, double fee, String address) {
        super(context, listener);
        mAmount = amount;
        mFee = fee;
        mAddress = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_eth_confirm_dialog_low : R.layout.pluto_withdraw_eth_confirm_dialog;
        setContentView(resId);
        //
        TextView txtAmount = findViewById(R.id.txt_amount);
        TextView txtFee = findViewById(R.id.txt_fee);
        TextView txtAddress = findViewById(R.id.txt_address);
        txtAmount.setText(ConvertUtils.double2Decimal(mAmount, 4, 2, true));
        txtFee.setText("Fee: " + ConvertUtils.double2Decimal(mFee, 4, 2, true) + "ETH");
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
