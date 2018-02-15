package com.ocoin.utils;

import android.support.annotation.NonNull;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.exceptions.TransactionTimeoutException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * 根据不同智能合约 生成对应的raw交易
 * Created by y on 2018/2/6.
 */

public class RawTransactionUtils {


    /**
     * 以太坊 交易 可以理解为与钱包地址进行交互
     *
     * @param nonce     交易次数
     * @param gas_price
     * @param gas_limit
     * @param toAddress
     * @param amount
     * @param data
     * @return
     */
    @NonNull
    public static RawTransaction getRawTransactionForEth(BigInteger nonce, String gas_price, String gas_limit, String toAddress, String amount, String data) {
        return RawTransaction.createTransaction(
                nonce,
                new BigInteger(gas_price),
                new BigInteger(gas_limit),
                toAddress,
                new BigDecimal(amount).multiply(ExchangeCalculator.ONE_ETHER).toBigInteger(),
                data
        );
    }


    /**
     * 代币交易 可以理解为与智能合约地址进行交互
     *
     * @param nonce           交易次数
     * @param contractAddress 智能合约地址
     * @param amount
     * @param gas_price
     * @param gas_limit
     * @param data1
     * @param toAddress       对象钱包地址
     * @return
     * @throws InterruptedException
     * @throws TransactionTimeoutException
     * @throws IOException
     */
    @NonNull
    public static RawTransaction getRawTransactionForToken(BigInteger nonce, String contractAddress, String amount, String gas_price, String gas_limit, String data1, String toAddress) throws InterruptedException, TransactionTimeoutException, IOException {
        String h = "1000000000000000000";//补足数 18位
        BigDecimal bigDecimal = new BigDecimal(h);
        String resultAmount = BigDecimal.valueOf(Double.valueOf(amount)).multiply(bigDecimal).setScale(0).toString();
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new Address(toAddress),
                        new Uint256(new BigInteger(resultAmount))),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);

        return RawTransaction.createTransaction(
                nonce,
                new BigInteger(gas_price),
                new BigInteger(gas_limit),
                contractAddress,
                BigInteger.ZERO,
                data
        );
    }

    /**
     *
     * @param
     * @param nonce
     * @param contractAddress  根据合约地址 是否为空判断是否为以太币 交易
     * @param amount
     * @param gas_price
     * @param gas_limit
     * @param data1
     * @param toAddress
     * @return
     * @throws InterruptedException
     * @throws TransactionTimeoutException
     * @throws IOException
     */
    public static RawTransaction getTransaction(  BigInteger nonce, String contractAddress, String amount, String gas_price, String gas_limit, String data1, String toAddress) throws InterruptedException, TransactionTimeoutException, IOException {
        if (contractAddress ==null) {
            return getRawTransactionForEth(nonce, gas_price, gas_limit, toAddress, amount, data1);
        } else {
            return getRawTransactionForToken(nonce, contractAddress, amount, gas_price, gas_limit, data1, toAddress);

        }
    }


}
