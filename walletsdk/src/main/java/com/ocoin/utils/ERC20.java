package com.ocoin.utils;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import rx.Observable;
import rx.functions.Func1;

//import org.web3j.protocol.core.RemoteCall;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 * <p>
 * <p>Generated with web3j version 3.2.0.
 */
public class ERC20 extends Contract {
    private static final String BINARY = "0x";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    public ERC20(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public ERC20(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    //    public RemoteCall<TransactionReceipt> approve(String spender, BigInteger value) {
//        Function function = new Function(
//                "approve",
//                Arrays.<Type>asList(new Address(spender),
//                new Uint256(value)),
//                Collections.<TypeReference<?>>emptyList());
//        return executeRemoteCallTransaction(function);
//    }
//
//    public RemoteCall<BigInteger> totalSupply() {
//        Function function = new Function("totalSupply",
//                Arrays.<Type>asList(),
//                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
//        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
//    }
//
    public Future<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
        Function function = new Function(
                "transferFrom",
                Arrays.<Type>asList(new Address(from),
                new Address(to),
                new Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }
//
    public <T extends Type> Future<T> balanceOf(String who) {
        Function function = new Function("balanceOf",
                Arrays.<Type>asList(new Address(who)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeCallSingleValueReturnAsync(function);
    }
//
    public Future<TransactionReceipt> transfer(String to, BigInteger value) throws InterruptedException, TransactionTimeoutException, IOException {
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new Address(to),
                new Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }
//
//    public RemoteCall<BigInteger> allowance(String owner, String spender) {
//        Function function = new Function("allowance",
//                Arrays.<Type>asList(new Address(owner),
//                new Address(spender)),
//                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
//        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
//    }
//
//    public static RemoteCall<ERC20> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
//        return deployRemoteCall(ERC20.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
//    }
//
//    public static RemoteCall<ERC20> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
//        return deployRemoteCall(ERC20.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
//    }

    public static ERC20 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC20(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ERC20 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC20(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class TransferEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }

    public static class ApprovalEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }
}
