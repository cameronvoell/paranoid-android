package com.example.cameron.ethereumtest1.model;

import java.util.ArrayList;

/**
 * Created by cameron on 4/16/18.
 */

public class EthereumTransaction {
    public String ethAddress;
    public String ethTxId;
    public int txActionId;
    public String txContent;
    public long txTimestamp;
    public long blockNumber;
    public boolean confirmed;
    public long gasCost;

    public EthereumTransaction(String ethAddress, String ethTxId, int txActionId,
                               String txContent, long txTimestamp, long blockNumber, boolean confirmed,
                               long gasCost) {
        this.ethAddress = ethAddress;
        this.ethTxId = ethTxId;
        this.txActionId = txActionId;
        this.txContent = txContent;
        this.txTimestamp = txTimestamp;
        this.blockNumber = blockNumber;
        this.confirmed = confirmed;
        this.gasCost = gasCost;
    }
}
