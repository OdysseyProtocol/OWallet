package com.ocoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.ocoin.activities.AddressDetailActivity;
import com.ocoin.activities.SendActivity;
import com.ocoin.utils.WalletStorage;

import java.util.ArrayList;
import java.util.UUID;

import rehanced.com.simpleetherwallet.R;

import com.ocoin.activities.WalletGenActivity;
import com.ocoin.interfaces.StorableWallet;
import com.ocoin.network.EtherscanAPI;
import com.ocoin.utils.Dialogs;
import com.ocoin.utils.MyLog;
import com.ocoin.utils.Settings;

/**
 * Created by y on 2018/2/5.
 */

public class WalletSDK {


    public static Context mContext;


    /**
     * sdk 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
    }


    /**
     * 开启测试模式
     *
     * @param isDebug true 开启测试
     */
    public static void DEBUG(boolean isDebug) {
        MyLog.DEBUG = isDebug;
        EtherscanAPI.DEBUG(isDebug);
    }


    /**
     * 创建钱包，只能创建一次
     *
     * @param ac
     */
    public static void generateWallet(Activity ac) {
        ArrayList<StorableWallet> storageWallets = WalletStorage.getInstance(ac).get();
        if (storageWallets != null && storageWallets.size() >= 1) {
            Toast.makeText(ac, "Wallet has already been created", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Settings.walletBeingGenerated) {
            Intent genI = new Intent(ac, WalletGenActivity.class);
            ac.startActivityForResult(genI, WalletGenActivity.REQUEST_CODE);
        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= 24) // Otherwise buttons on 7.0+ are nearly invisible
                builder = new AlertDialog.Builder(ac, R.style.AlertDialogTheme);
            else
                builder = new AlertDialog.Builder(ac);
            builder.setTitle(R.string.wallet_one_at_a_time);
            builder.setMessage(R.string.wallet_one_at_a_time_text);
            builder.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        }
    }


    /**
     * 自主选择交易
     *
     * @param ac
     * @param address         钱包地址
     * @param contractAddress 智能合约地址，如果地址为null，则进行以太币交易
     * @return 返回uuid 作为request 的凭证，用于匹配结果
     */

    public static String sendTransaction(Activity ac, String address, String contractAddress) {
        if (WalletStorage.getInstance(ac).getFullOnly().size() == 0) {
            Dialogs.noFullWallet(ac);
            return "";
        } else {
            Intent intent = new Intent(ac, SendActivity.class);
            if (contractAddress != null) {
                intent.putExtra("TOKEN_ADDRESS", contractAddress);
            }
            intent.putExtra("FROM_ADDRESS", address);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            intent.putExtra("REQUEST_UUID", uuid);
            ac.startActivityForResult(intent, SendActivity.REQUEST_CODE);
            return uuid;
        }
    }

    /**
     * 指定对象和额度交易 无法更改amount
     *
     * @param ac
     * @param fromAddress     钱包地址
     * @param toAddress       对方钱包地址
     * @param contractAddress 智能合约地址，如果地址为null，则进行以太币交易
     * @param amount          指定虚拟币交易额
     * @return 返回uuid 作为request 的凭证，用于匹配结果
     */
    public static String sendTransaction(Activity ac, String fromAddress, String toAddress, String contractAddress, String amount) {
        if (WalletStorage.getInstance(ac).getFullOnly().size() == 0) {
            Dialogs.noFullWallet(ac);
            return "";
        } else {
            Intent intent = new Intent(ac, SendActivity.class);
            intent.putExtra("FROM_ADDRESS", fromAddress);
            intent.putExtra("TO_ADDRESS", toAddress);
            intent.putExtra("AMOUNT", amount);

            if (contractAddress != null) {
                intent.putExtra("TOKEN_ADDRESS", contractAddress);
            }
            String uuid = UUID.randomUUID().toString().replace("-", "");
            intent.putExtra("REQUEST_UUID", uuid);
            ac.startActivityForResult(intent, SendActivity.REQUEST_CODE);
            return uuid;
        }
    }


    /**
     * 获得默认钱包地址
     *
     * @param ctx
     * @return
     */
    public static String getWalletAddress(Context ctx) {
        ArrayList<StorableWallet> walletStorage = WalletStorage.getInstance(ctx).get();
        if (walletStorage == null || walletStorage.size() < 1) {
            Toast.makeText(ctx, "please generate new  wallet", Toast.LENGTH_SHORT).show();
            return null;
        }
        return walletStorage.get(0).getPubKey();
    }


    /**
     * 打开钱包，查看钱包财产
     *
     * @param ac
     */
    public static void openOwnWallet(Activity ac) {
        ArrayList<StorableWallet> storableWallets = WalletStorage.getInstance(ac).get();
        if (storableWallets == null || storableWallets.size() < 1) {
            Toast.makeText(ac, "please generate new  wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent detail = new Intent(ac, AddressDetailActivity.class);
        detail.putExtra("ADDRESS", storableWallets.get(0).getPubKey());
        detail.putExtra("TYPE", AddressDetailActivity.OWN_WALLET);
        ac.startActivity(detail);
    }


}
