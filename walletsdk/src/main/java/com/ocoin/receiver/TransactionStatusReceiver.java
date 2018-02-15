package com.ocoin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by y on 2018/2/9.
 */

public abstract class TransactionStatusReceiver extends BroadcastReceiver {
    //交易失败
    public static final String TX_STATUS_FAIL = "-1";
    //订单创建成功
    public static final String TX_STATUS_BUILD = "1";
    //订单交易成功
    public static final String TX_STATUS_SUCCESS = "2";
    //filter action
    public static final String TX_ACTION = "TX_ACTION";
    //订单 hash
    public static final String TX_HASH = "TX_HASH";
    public static final String REQUEST_UUID = "REQUEST_UUID";
    //订单状态
    public static final String TX_STATUS = "TX_STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String txUUid = intent.getStringExtra(REQUEST_UUID);
        String txHash = intent.getStringExtra(TX_HASH);
        if (TX_STATUS_FAIL.equals(intent.getStringExtra(TX_STATUS))) {
            onFail(txUUid, txHash);
        } else if (TX_STATUS_BUILD.equals(intent.getStringExtra(TX_STATUS))) {
            onBuildSuccess(txUUid, txHash);
        } else if (TX_STATUS_SUCCESS.equals(intent.getStringExtra(TX_STATUS))) {
            onTxSuccess(txUUid, txHash);

        }
    }

    public abstract void onFail(String requestUUid, String txHash);

    public abstract void onBuildSuccess(String requestUUid, String txHash);

    public abstract void onTxSuccess(String requestUUid, String txHash);


}
