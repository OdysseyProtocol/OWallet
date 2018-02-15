package com.ocoin.demo;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.ocoin.WalletSDK;
import com.ocoin.activities.WalletMainActivity;
import com.ocoin.tempdemo.R;

import static com.ocoin.WalletSDK.getWalletAddress;

public class BankActivity extends AppCompatActivity {


    @BindView(R.id.btn_generate_new_wallet)
    Button btnGenerateNewWallet;
    @BindView(R.id.btn_get_eth_coin)
    Button btnGetEthCoin;
    @BindView(R.id.btn_transaction_ocn)
    Button btnTransactionOcn;
    @BindView(R.id.btn_transaction_eth)
    Button btnTransactionEth;

    @BindView(R.id.btn_transaction_specify)
    Button btnTransactionSpecify;

    private BroadcastReceiver receiver = new MyTransactionStatusReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //generate wallet
        btnGenerateNewWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletSDK.generateWallet(BankActivity.this);
            }
        });
        //token list
        btnGetEthCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletSDK.openOwnWallet(BankActivity.this);

            }
        });
        //OCN  TX
        btnTransactionOcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletSDK.sendTransaction(BankActivity.this, getWalletAddress(BankActivity.this), "0xd1bcbe82f40a9d7fbcbd28cca6043d72d66d8e9d");
            }
        });
        //ETH TX
        btnTransactionEth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletSDK.sendTransaction(BankActivity.this, getWalletAddress(BankActivity.this), null);

            }
        });


        final String testAddress = "0x14fef048b878132c4cdcf7819a66b1eaa9ce8fc2";
        btnTransactionSpecify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletSDK.sendTransaction(BankActivity.this, getWalletAddress(BankActivity.this), testAddress, null, 2 + "");

            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("TX_ACTION");
        this.registerReceiver(receiver, intentFilter);

    }


    @Override
    protected void onDestroy() {
        if (receiver != null)
            this.unregisterReceiver(receiver);
        super.onDestroy();
    }


}
