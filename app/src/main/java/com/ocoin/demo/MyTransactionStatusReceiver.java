package com.ocoin.demo;

import com.ocoin.receiver.TransactionStatusReceiver;
import com.ocoin.utils.MyLog;

/**
 * Created by y on 2018/2/9.
 */

public class MyTransactionStatusReceiver extends TransactionStatusReceiver {

    @Override
    public void onFail(String requestUUid, String txHash) {
        MyLog.i("requestUUid:" + requestUUid + "txHash" + txHash);
    }

    @Override
    public void onBuildSuccess(String requestUUid, String txHash) {
        MyLog.i("requestUUid:" + requestUUid + "txHash" + txHash);

    }

    @Override
    public void onTxSuccess(String requestUUid, String txHash) {
        MyLog.i("requestUUid:" + requestUUid + "txHash" + txHash);

    }
}
