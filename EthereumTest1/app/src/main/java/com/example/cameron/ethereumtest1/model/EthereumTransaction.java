package com.example.cameron.ethereumtest1.model;

import java.util.ArrayList;

/**
 * Created by cameron on 4/16/18.
 */

public class EthereumTransaction {
    String ETH_ID;
    String TX_ID;
    long timestamp;
    long blockNumber;
    int ACTION;
    ArrayList<String> INPUTS;
    long gasCost;
    float ethPrice;
    int ERROR_ID;
}
