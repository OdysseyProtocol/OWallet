package com.ocoin.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.ocoin.activities.WalletMainActivity;
import com.ocoin.bean.TransactionReceipt;
import com.ocoin.network.EtherscanAPI;
import com.ocoin.receiver.TransactionStatusReceiver;
import com.ocoin.utils.MyLog;
import com.ocoin.utils.RawTransactionUtils;
import com.ocoin.utils.WalletStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.request.RawTransaction;

import java.io.IOException;
import java.math.BigInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rehanced.com.simpleetherwallet.R;

public class TransactionService extends IntentService {

    private NotificationCompat.Builder builder;
    final int mNotificationId = 153;

    public TransactionService() {
        super("Transaction Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification();
        try {
            final String fromAddress = intent.getStringExtra("FROM_ADDRESS");
            final String toAddress = intent.getStringExtra("TO_ADDRESS");
            final String amount = intent.getStringExtra("AMOUNT");
            final String gas_price = intent.getStringExtra("GAS_PRICE");
            final String gas_limit = intent.getStringExtra("GAS_LIMIT");
            final String requestUuid = intent.getStringExtra("REQUEST_UUID");
            final String data = intent.getStringExtra("DATA");
            String password = intent.getStringExtra("PASSWORD");
            final String contractAddress = intent.getStringExtra("CONTRACT_ADDRESS");
            final Credentials keys = WalletStorage.getInstance(getApplicationContext()).getFullWallet(getApplicationContext(), password, fromAddress);

            EtherscanAPI.getInstance().getNonceForAddress(fromAddress, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    error("Can't connect to network, retry it later", requestUuid);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        JSONObject o = new JSONObject(response.body().string());
                        BigInteger nonce = new BigInteger(o.getString("result").substring(2), 16);
                        RawTransaction tx = RawTransactionUtils.getTransaction(nonce, contractAddress, amount, gas_price, gas_limit, data, toAddress);

                        Log.d("txx", "Nonce: " + tx.getNonce() + "\n" +
                                "gasPrice: " + tx.getGasPrice() + "\n" +
                                "gasLimit: " + tx.getGasLimit() + "\n" +
                                "To: " + tx.getTo() + "\n" +
                                "Amount: " + tx.getValue() + "\n" +
                                "Data: " + tx.getData());

                        byte[] signed = TransactionEncoder.signMessage(tx, (byte) EtherscanAPI.CHAIN_ID, keys);

                        forwardTX(signed, requestUuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error("Can't connect to network, retry it later", requestUuid);
                    }
                }
            });

        } catch (Exception e) {
            error("Invalid Wallet Password!", null);
            e.printStackTrace();
        }
    }


    private void forwardTX(byte[] signed, final String requestUuid) throws IOException {
        EtherscanAPI.getInstance().forwardTransaction("0x" + Hex.toHexString(signed), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                error("Can't connect to network, retry it later",requestUuid);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String received = response.body().string();
                try {
                    String hash = new JSONObject(received).getString("result");
                   startBroadcast(hash, requestUuid, TransactionStatusReceiver.TX_STATUS_SUCCESS);
                    getTransactionStatus(hash, requestUuid);
                } catch (Exception e) {
                    // Advanced error handling. If etherscan returns error message show the shortened version in notification. Else abbort with unknown error
                    try {
                        String errormsg = new JSONObject(received).getJSONObject("error").getString("message");
                        if (errormsg.indexOf(".") > 0)
                            errormsg = errormsg.substring(0, errormsg.indexOf("."));
                        error(errormsg,requestUuid); // f.E Insufficient funds
                    } catch (JSONException e1) {
                        error("Unknown error occured",null);
                    }
                }
            }
        });
    }


    /**
     * 三种状态
     * 交易失败   status： 0; 交易成功 status:1 && type:mined ;交易中：status:1 && type:pending
     *
     * @param txHash
     * @throws IOException
     */
    private void getTransactionStatus(final String txHash, final String requestUuid) throws IOException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EtherscanAPI.getInstance().getTransactionReceipt(txHash, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                error("err:" + e.toString(),requestUuid);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    String result = new JSONObject(string).getString("result");
                    MyLog.i("onResponse_result_getTransactionReceipt" + result);

                    Gson gson = new Gson();
                    TransactionReceipt transactionReceipt = gson.fromJson(result, TransactionReceipt.class);
                    if (transactionReceipt.status == 0) {
                        error("Transaction Failure",requestUuid);
                    } else if (transactionReceipt.status == 1) {
                            if (transactionReceipt.blockHash != null && transactionReceipt.blockHash.contains("0x")) {
                                MyLog.i("status" + "mined" + "txhash:" + txHash);
                                suc(txHash, requestUuid);
                            } else {
                                MyLog.i("status" + "pending" + "txhash:" + txHash);
                                getTransactionStatus(txHash, requestUuid);
                            }

                        }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void suc(String hash, String requestUuid) {
        MyLog.i("txHash" + hash);
        startBroadcast(hash, requestUuid, TransactionStatusReceiver.TX_STATUS_BUILD);
        builder
                .setContentTitle(getString(R.string.notification_transfersuc))
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText("");

        Intent main = new Intent(this, WalletMainActivity.class);
        main.putExtra("STARTAT", 2);
        main.putExtra("TXHASH", hash);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }


    private void startBroadcast(String hash, String requestUuid, String status) {
        Intent intent = new Intent();
        intent.setAction(TransactionStatusReceiver.TX_ACTION);
        if (hash != null) {
            intent.putExtra(TransactionStatusReceiver.TX_HASH, hash);
        }
        intent.putExtra(TransactionStatusReceiver.REQUEST_UUID, requestUuid);
        intent.putExtra(TransactionStatusReceiver.TX_STATUS, status);
        sendBroadcast(intent);


    }

    private void error(String err, String requestUuid) {
        if(requestUuid!=null)
        startBroadcast(null, requestUuid, TransactionStatusReceiver.TX_STATUS_FAIL);
        builder
                .setContentTitle(getString(R.string.notification_transferfail))
                .setProgress(100, 100, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentText(err);

        Intent main = new Intent(this, WalletMainActivity.class);
        main.putExtra("STARTAT", 2);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                main, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void sendNotification() {
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(0x2d435c)
                .setTicker(getString(R.string.notification_transferingticker))
                .setContentTitle(getString(R.string.notification_transfering_title))
                .setContentText(getString(R.string.notification_might_take_a_minute))
                .setOngoing(true)
                .setProgress(0, 0, true);
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, builder.build());
    }


}
