package com.ocoin.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;


import com.ocoin.APIKey;
import com.ocoin.utils.RequestCache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;

import com.ocoin.interfaces.LastIconLoaded;
import com.ocoin.interfaces.StorableWallet;
import com.ocoin.utils.Key;
import com.ocoin.utils.MyLog;
import com.ocoin.utils.TokenIconCache;

public class EtherscanAPI {


    /**
     * 测试组合
     *
     * @param ROPSTEN_API           测试用的api
     * @param ROPSTEN_CHAIN_ID     测试用区块id
     * <p>
     * 正式组合
     * @param ROPSTEN_API       正式api
     * @param MAIN_CHAIN_ID     正式区块id
     */
    private String token;
    public static int MAIN_CHAIN_ID = 1;
    public static int ROPSTEN_CHAIN_ID = 3;

    public static String ETH_API = "http://api.etherscan.io/api?";
    public static String ROPSTEN_API = "https://ropsten.etherscan.io/api?";

    public static String Url = ROPSTEN_API;
    public static int CHAIN_ID = ROPSTEN_CHAIN_ID;


    public static  void DEBUG(boolean isTest) {
        Url = (isTest) ? ROPSTEN_API : ETH_API;
        CHAIN_ID = (isTest) ? ROPSTEN_CHAIN_ID : MAIN_CHAIN_ID;
    }

    private static EtherscanAPI instance;

    public static EtherscanAPI getInstance() {
        if (instance == null) {
            instance = new EtherscanAPI();
        }
        return instance;
    }

    public void getPriceChart(long starttime, int period, boolean usd, Callback b) throws IOException {
        get("http://poloniex.com/public?command=returnChartData&currencyPair=" + (usd ? "USDT_ETH" : "BTC_ETH") + "&start=" + starttime + "&end=9999999999&period=" + period, b);
    }


    /**
     * Retrieve all internal transactions from address like contract calls, for normal transactions @see EtherscanAPI#getNormalTransactions() )
     *
     * @param address Ether address
     * @param b       Network callback to @see FragmentTransactions#update() or @see FragmentTransactionsAll#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getInternalTransactions(String address, Callback b, boolean force) throws IOException {
        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TXS_INTERNAL, address)) {
            b.onResponse(null, new okhttp3.Response.Builder().code(200).message("").request(new Request.Builder()
                    .url(Url + " module=account&action=txlistinternal&address=" + address + "&startblock=0&endblock=99999999&sort=asc&apikey=" + token)
                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TXS_INTERNAL, address))).build());
            return;
        }
        get(Url + "module=account&action=txlistinternal&address=" + address + "&startblock=0&endblock=99999999&sort=asc&apikey=" + token, b);
    }


    /**
     * Retrieve all normal ether transactions from address (excluding contract calls etc, @see EtherscanAPI#getInternalTransactions() )
     *
     * @param address Ether address
     * @param b       Network callback to @see FragmentTransactions#update() or @see FragmentTransactionsAll#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getNormalTransactions(String address, Callback b, boolean force) throws IOException {
        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TXS_NORMAL, address)) {
            b.onResponse(null, new okhttp3.Response.Builder().code(200).message("").request(new Request.Builder()
                    .url(Url + "module=account&action=txlist&address=" + address + "&startblock=0&endblock=99999999&sort=asc&apikey=" + token)
                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TXS_NORMAL, address))).build());
            return;
        }
        get(Url + "module=account&action=txlist&address=" + address + "&startblock=0&endblock=99999999&sort=asc&apikey=" + token, b);
    }


    public void getEtherPrice(Callback b) throws IOException {
        get(Url + "module=stats&action=ethprice&apikey=" + token, b);
    }


    public void getGasPrice(Callback b) throws IOException {
        get(Url + "module=proxy&action=eth_gasPrice&apikey=" + token, b);
    }


    /**
     * Get token balances via ethplorer.io
     *
     * @param address Ether address
     * @param b       Network callback to @see FragmentDetailOverview#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getTokenBalances(String address, Callback b, boolean force) throws IOException {
        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TOKEN, address)) {
            b.onResponse(null, new okhttp3.Response.Builder().code(200).message("").request(new Request.Builder()
                    .url("https://api.ethplorer.io/getAddressInfo/" + address + "?apiKey=freekey")
                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TOKEN, address))).build());
            return;
        }
        get("http://api.ethplorer.io/getAddressInfo/" + address + "?apiKey=freekey", b);
    }


    /**
     * Download and save token icon in permanent image cache (TokenIconCache)
     *
     * @param c         Application context, used to load TokenIconCache if reinstanced
     * @param tokenName Name of token
     * @param lastToken Boolean defining whether this is the last icon to download or not. If so callback is called to refresh recyclerview (notifyDataSetChanged)
     * @param callback  Callback to @see FragmentDetailOverview#onLastIconDownloaded()
     * @throws IOException Network exceptions
     */
    public void loadTokenIcon(final Context c, String tokenName, final boolean lastToken, final LastIconLoaded callback) throws IOException {
        if (tokenName.indexOf(" ") > 0)
            tokenName = tokenName.substring(0, tokenName.indexOf(" "));
        if (TokenIconCache.getInstance(c).contains(tokenName)) return;

        if (tokenName.equalsIgnoreCase("OMGToken"))
            tokenName = "omise";
        else if (tokenName.equalsIgnoreCase("0x"))
            tokenName = "0xtoken_28";

        final String tokenNamef = tokenName;
        get("http://etherscan.io//token/images/" + tokenNamef + ".PNG", new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (c == null) return;
                ResponseBody in = response.body();
                InputStream inputStream = in.byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                final Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                TokenIconCache.getInstance(c).put(c, tokenNamef, new BitmapDrawable(c.getResources(), bitmap).getBitmap());
                // if(lastToken) // TODO: resolve race condition
                callback.onLastIconDownloaded();
            }
        });
    }


    public void getGasLimitEstimate(String to, Callback b) throws IOException {
        get(Url + "module=proxy&action=eth_estimateGas&to=" + to + "&value=0xff22&gasPrice=0x051da038cc&gas=0xffffff&apikey=" + token, b);
    }


    public void getBalance(String address, Callback b) throws IOException {
        get(Url + "module=account&action=balance&address=" + address + "&apikey=" + token, b);
    }


    public void getNonceForAddress(String address, Callback b) throws IOException {
        get(Url + "module=proxy&action=eth_getTransactionCount&address=" + address + "&tag=latest&apikey=" + token, b);
    }


    public void getPriceConversionRates(String currencyConversion, Callback b) throws IOException {
        get("https://api.fixer.io/latest?base=USD&symbols=" + currencyConversion, b);
    }


    public void getBalances(ArrayList<StorableWallet> addresses, Callback b) throws IOException {
        String url = Url + "module=account&action=balancemulti&address=";
        for (StorableWallet address : addresses)
            url += address.getPubKey() + ",";
        url = url.substring(0, url.length() - 1) + "&tag=latest&apikey=" + token; // remove last , AND add token
        get(url, b);
    }


    public void forwardTransaction(String raw, Callback b) throws IOException {
        get(Url + "module=proxy&action=eth_sendRawTransaction&hex=" + raw + "&apikey=" + token, b);
    }


    /**
     * get balance of token
     *
     * @param to
     * @param data
     * @param b
     * @throws IOException
     */
    public void eth_call(String to, String data, Callback b) throws IOException {

        get(Url + "module=proxy&action=eth_call&to=" + to + "&data=" + data + "&tag=latest&apikey=" + token, b);


    }


    /**
     * https://ropsten.etherscan.io/api?module=transaction&action=getstatus&txhash=0xf471371a95e4d18fa86867b7b3375b463f73b911f7c61dfcce64a7a9f455aaaa&apikey=UJQ2R2QSC3NE9NVZEVUA8AZJVYFBCP11KT
     *
     * @param txhash 交易号 hash
     * @param b
     * @throws IOException
     */
    public void get_transaction_status(String txhash, Callback b) throws IOException {
        String url = Url + "module=transaction&action=getstatus&txhash=" + txhash + "&apikey=" + token;
        get(url, b);
    }


    /**
     *
     *
     *
     *
     *
     *
     */

    /**
     * eg:      https://ropsten.etherscan.io/api?module=proxy&action=eth_getTransactionReceipt&txhash=0xe33b2e37a4aec55bcd9776f9f6f93f7f4b4c28e10ce7c88ae1901b12eb524f02&apikey=UJQ2R2QSC3NE9NVZEVUA8AZJVYFBCP11KT
     *
     * @param txhash 交易 hash
     * @param b
     * @throws IOException
     */
    public void getTransactionReceipt(String txhash, Callback b) throws IOException {
        String url = Url + "module=proxy&action=eth_getTransactionReceipt&txhash=" + txhash + "&apikey=" + token;
        get(url, b);
    }


    public void get(String url, Callback b) throws IOException {

        MyLog.i("GET==>URL:" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(b);
    }


    private EtherscanAPI() {
        token = new Key(APIKey.API_KEY).toString();
    }

}
