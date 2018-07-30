package com.example.cameron.ethereumtest1.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cameron on 10/24/17.
 */

public class PrefUtils {

    private final static String SHARED_PREFERENCES = "digital_desert_preferences5";
    private final static int PREF_MODE = android.content.Context.MODE_PRIVATE;

    private final static String PREF_SELECTED_FRAGMENT = "pref_selected_fragment";
    public final static int SELECTED_CONTENT_LIST = 0;
    public final static int SELECTED_PUBLICATION_LIST = 1;
    public final static int SELECTED_USER_FRAGMENT = 2;
    public final static int SELECTED_TRANSACTION_FRAGMENT = 3;

    private static final String PREF_SELECTED_ACCOUNT_NUM = "pref_selected_account_num";
    private static final String PREF_SELECTED_ACCOUNT_ADDRESS = "pref_selected_account_address";

//    private static final String PREF_SELECTED_ACCOUNT_BALANCE = "pref_selected_account_balance";
    private static final String PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED = "pref_selected_account_balance";
    private static final long ACCOUNT_UPDATE_INTERVAL_MS = 1000 * 60 * 1; //1 min

    private static final String PREF_SELECTED_ACCOUNT_USER_NAME = "pref_selected_account_user_name";
    private static final String PREF_SELECTED_ACCOUNT_USER_ICON_URL = "pref_selected_account_user_icon_image_url";
    private static final String PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED = "pref_selected_account_user_name_last_checked";

    private static final String PREF_SELECTED_ACCOUNT_CONTENT_LIST_LAST_CHECKED = "pref_selected_account_content_list_last_checked";
    private static final String PREF_SELECTED_ACCOUNT_BALANCE_STRING = "pref.selected.account.balance.string";

    private static final String PREF_SELECTED_PUBLICATION = "pref.selected.publication";

    private static SharedPreferences sp(Context baseContext) {
        return baseContext.getSharedPreferences(SHARED_PREFERENCES, PREF_MODE);
    }

    public static int getSelectedFragment(Context ctx) {
        return sp(ctx).getInt(PREF_SELECTED_FRAGMENT, SELECTED_USER_FRAGMENT);
    }

    public static void saveSelectedFragment(Context ctx, int whichFragment) {
        sp(ctx).edit().putInt(PREF_SELECTED_FRAGMENT, whichFragment).commit();
    }

    public static long getSelectedAccountNum(Context ctx) {
        return sp(ctx).getInt(PREF_SELECTED_ACCOUNT_NUM, 0);
    }

    public static String getSelectedAccountAddress(Context ctx) {
        return sp(ctx).getString(PREF_SELECTED_ACCOUNT_ADDRESS, "");
    }

    public static void saveSelectedAccount(Context ctx, long selectedAccount, String selectedAddress) {
        sp(ctx).edit().putInt(PREF_SELECTED_ACCOUNT_NUM, (int)selectedAccount).commit();
        sp(ctx).edit().putString(PREF_SELECTED_ACCOUNT_ADDRESS, selectedAddress).commit();
    }

    public static String getSelectedAccountBalance(Context ctx) {
        return sp(ctx).getString(PREF_SELECTED_ACCOUNT_BALANCE_STRING + getSelectedAccountAddress(ctx), "empty?");
    }

    public static boolean shouldUpdateAccountBalance(Context ctx) {
        long lastUpdateTime = sp(ctx).getLong(PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED + getSelectedAccountAddress(ctx), 0);
        return System.currentTimeMillis() - lastUpdateTime > ACCOUNT_UPDATE_INTERVAL_MS;
    }

    public static void saveSelectedAccountBalance(Context ctx, String balance) {
        sp(ctx).edit().putString(PREF_SELECTED_ACCOUNT_BALANCE_STRING + getSelectedAccountAddress(ctx), balance).commit();
        sp(ctx).edit().putLong(PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED + getSelectedAccountAddress(ctx), System.currentTimeMillis()).commit();
    }

    public static String getSelectedAccountUserName(Context ctx) {
        return sp(ctx).getString(PREF_SELECTED_ACCOUNT_USER_NAME + getSelectedAccountAddress(ctx), "not yet registered...");
    }

    public static boolean shouldUpdateAccountUserName(Context ctx) {
        long lastUpdateTime = sp(ctx).getLong(PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED + getSelectedAccountAddress(ctx), 0);
        return System.currentTimeMillis() - lastUpdateTime > ACCOUNT_UPDATE_INTERVAL_MS;
    }

    public static void saveSelectedAccountUserName(Context ctx, String userName) {
        sp(ctx).edit().putString(PREF_SELECTED_ACCOUNT_USER_NAME + getSelectedAccountAddress(ctx), userName).commit();
        sp(ctx).edit().putLong(PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED + getSelectedAccountAddress(ctx), System.currentTimeMillis()).commit();
    }

    public static boolean shouldUpdateAccountContentList(Context ctx) {
        long lastUpdateTime = sp(ctx).getLong(PREF_SELECTED_ACCOUNT_CONTENT_LIST_LAST_CHECKED + getSelectedAccountAddress(ctx), 0);
        return System.currentTimeMillis() - lastUpdateTime > ACCOUNT_UPDATE_INTERVAL_MS;
    }

    public static String getSelectedAccountUserIconImageUrl(Context ctx) {
        return sp(ctx).getString(PREF_SELECTED_ACCOUNT_USER_ICON_URL + getSelectedAccountAddress(ctx), "meta");
    }

    public static void saveSelectedAccountUserIconImageURLContext (Context ctx, String userIconImageUrl) {
        sp(ctx).edit().putString(PREF_SELECTED_ACCOUNT_USER_ICON_URL + getSelectedAccountAddress(ctx), userIconImageUrl).commit();
    }

    public static void saveSelectedPublication(Context ctx, int position) {
        sp(ctx).edit().putInt(PREF_SELECTED_PUBLICATION, position).commit();
    }

    public static int getSelectedPublication(Context ctx) {
        return sp(ctx).getInt(PREF_SELECTED_PUBLICATION, 0);
    }
}
