package com.ocoin.bean;

import java.util.List;

/**
 * Created by y on 2018/2/9.
 */

public class TransactionReceipt {
    public String blockHash;
    public String blockNumber;
    public String contractAddress;
    public String cumulativeGasUsed;
    public String gasUsed;
    public List<Logs> logs;
    public String logsBloom;
    public String root;
    public int status;
    public String transactionHash;
    public String transactionIndex;


}
