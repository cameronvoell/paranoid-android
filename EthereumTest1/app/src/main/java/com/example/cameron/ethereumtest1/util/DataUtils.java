package com.example.cameron.ethereumtest1.util;

import com.example.cameron.ethereumtest1.database.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by cameron on 10/13/17.
 */

public class DataUtils {

    public static String convertTimeStampToDateString(long timestamp) {
        Date date = new Date(timestamp);
        long now = System.currentTimeMillis();
        if ((now - timestamp) < (1000 * 60 * 60)) {
            long timePassed = now - timestamp;
            long hours = timePassed / (1000 * 60);
            return hours + " mins ago";
        } else if ((now - timestamp) < (1000 * 60 * 60 * 24)) {
            long timePassed = now - timestamp;
            long hours = timePassed / (1000 * 60 * 60);
            return hours + " hours ago";
        }
        return (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
    }

    public static String formatEthereumAccount(String account) {
        return account.substring(0,4) + "..." + account.substring(account.length() -4,account.length() - 1);
    }

    public static String formatTransactionHash(String account) {
        return "tx:" + account.substring(0,4) + "..." + account.substring(account.length() -4,account.length() - 1);
    }

    public static String convertActionIdForDisplay(int actionId) {
        switch (actionId) {
            case DatabaseHelper.TX_ACTION_ID_SEND_ETH:
                return "SEND ETH";
            case DatabaseHelper.TX_ACTION_ID_REGISTER:
                return "REGISTER";
            case DatabaseHelper.TX_ACTION_ID_UPDATE_USER_PIC:
                return "UPDATE PIC";
            case DatabaseHelper.TX_ACTION_ID_PUBLISH_USER_CONTENT:
                return "PUBLISH USER";
            case DatabaseHelper.TX_ACTION_ID_PUBLISH_TO_PUBLICATION:
            default:
                return "PUBLISH PUBLICATION";

        }
    }

    public static String formatAccountBalanceEther(String weiBalance, int numDecimals) {
        String weiString = String.valueOf(weiBalance);
        String ethString = "";
        if (weiString.length() > 18) {
            ethString = weiString.substring(0, weiString.length() - 18) + "." + weiString.substring(weiString.length() - 18, weiString.length() - 18 + numDecimals);
        } else {
            ethString = "0.";
            int zeroCounter = 18 - weiString.length();
            for (int i=0; i<zeroCounter && i < numDecimals; i++) {
                ethString += "0";
            }
            int numsRemaining = numDecimals - (ethString.length() - 2);
            ethString += weiString.substring(0, numsRemaining);
        }
        return ethString + " ETH";
    }

    public static String formatBlockNumber(long blockNumber) {
        String.valueOf(blockNumber);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String blockNumberString = formatter.format(blockNumber);
        return "#" + blockNumberString;
    }
}
