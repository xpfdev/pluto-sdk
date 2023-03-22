package com.pluto.view;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pluto.entity.History;
import com.pluto.sdk.CommNetResponseListener;
import com.pluto.sdk.CoreSDK;
import com.pluto.sdk.R;
import com.pluto.utils.ConvertUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WithdrawHistoryActivity extends PlutoActivity {
    //
    private static final String TAG = "WithdrawHistoryActivity";
    //
    private RecyclerView mRvHistory;
    //
    private TextView mTxtEmpty;
    //
    List<History> mHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_history_activity_low : R.layout.pluto_withdraw_history_activity;
        setContentView(resId);
        //
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> {
            finish();
        });
        mRvHistory = findViewById(R.id.rv_history);
        mTxtEmpty = findViewById(R.id.txt_empty);
        CoreSDK.getInstance().getWithdrawHistory(new CommNetResponseListener() {
            @Override
            public void onResponse(boolean success, String message) {
                if (WithdrawHistoryActivity.this.isDestroyed()) {
                    return;
                }
                //
                if (!success) {
                    if (message != null && !message.equals("")) {
                        Toast.makeText(WithdrawHistoryActivity.this, message, Toast.LENGTH_SHORT);
                    }
                    return;
                }
                //
                mHistoryList = CoreSDK.getInstance().getAccount().getListHistory();
                mRvHistory.setAdapter(new HistoryAdapter());
                mRvHistory.setLayoutManager(new LinearLayoutManager(WithdrawHistoryActivity.this));
                //
                if (mHistoryList.size() <= 0) {
                    mRvHistory.setVisibility(View.INVISIBLE);
                    mTxtEmpty.setVisibility(View.VISIBLE);
                } else {
                    mRvHistory.setVisibility(View.VISIBLE);
                    mTxtEmpty.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     *
     */
    class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resId = CoreSDK.getLowScreen() ? R.layout.pluto_withdraw_history_item_activity_low : R.layout.pluto_withdraw_history_item_activity;
            View view = View.inflate(WithdrawHistoryActivity.this, resId, null);
            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
            History data = mHistoryList.get(position);
            holder.mTxtInfo.setText(data.getType() == 2 ? "ETH Withdraw" : "FISH/ETH");
            if (data.getType() == 2) {
                holder.mTxtInfo.setText("ETH Withdraw");
                holder.mTxtFromAmount.setText(ConvertUtils.double2Decimal(data.getFromAmount(), 4, 2, true) + "ETH");
                holder.mTxtToAmount.setVisibility(View.GONE);
                holder.mTxtFee.setText("Fee " + ConvertUtils.double2Decimal(data.getFee(), 4, 2, true) + " ETH");
            } else {
                holder.mTxtInfo.setText("FISH/ETH");
                holder.mTxtFromAmount.setText(ConvertUtils.num2Thousandth((long) data.getFromAmount()) + "FISH");
                holder.mTxtToAmount.setVisibility(View.VISIBLE);
                holder.mTxtToAmount.setText("≈" + ConvertUtils.double2Decimal(data.getToAmount(), 4, 2, true) + "ETH");
                holder.mTxtFee.setText("Fee " + ConvertUtils.num2Thousandth((int) data.getFee()) + " FISH");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.mTxtTime.setText(sdf.format(data.getTime()));
        }

        @Override
        public int getItemCount() {
            return mHistoryList.size();
        }
    }

    /**
     *
     */
    class HistoryHolder extends RecyclerView.ViewHolder {
        //
        TextView mTxtInfo;
        //
        TextView mTxtFromAmount;
        //
        TextView mTxtTime;
        //
        TextView mTxtToAmount;
        //
        TextView mTxtFee;
        /**
         *
         * @param itemView
         */
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            mTxtInfo = itemView.findViewById(R.id.txt_info);
            mTxtFromAmount = itemView.findViewById(R.id.txt_from_amount);
            mTxtTime = itemView.findViewById(R.id.txt_time);
            mTxtToAmount = itemView.findViewById(R.id.txt_to_amount);
            mTxtFee = itemView.findViewById(R.id.txt_fee);
        }
    }
}