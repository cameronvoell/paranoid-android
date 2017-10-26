package com.example.cameron.ethereumtest1.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cameron on 10/24/17.
 */

public class PrefUtils {

    private final static String SHARED_PREFERENCES = "digital_desert_preferences4";
    private final static int PREF_MODE = android.content.Context.MODE_PRIVATE;

    private final static String PREF_SELECTED_FRAGMENT = "pref_selected_fragment";
    public final static int SELECTED_CONTENT_LIST = 0;
    public final static int SELECTED_PUBLICATION_LIST = 1;
    public final static int SELECTED_USER_FRAGMENT = 2;

    private static final String PREF_SELECTED_ACCOUNT = "pref_selected_account";

    private static final String PREF_SELECTED_ACCOUNT_BALANCE = "pref_selected_account_balance";
    private static final String PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED = "pref_selected_account_balance";
    private static final long ACCOUNT_UPDATE_INTERVAL_MS = 1000 * 60 * 1; //1 min

    private static final String PREF_SELECTED_ACCOUNT_USER_NAME = "pref_selected_account_user_name";
    private static final String PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED = "pref_selected_account_user_name_last_checked";

    private static SharedPreferences sp(Context baseContext) {
        return baseContext.getSharedPreferences(SHARED_PREFERENCES, PREF_MODE);
    }

    public static int getSelectedFragment(Context ctx) {
        return sp(ctx).getInt(PREF_SELECTED_FRAGMENT, SELECTED_USER_FRAGMENT);
    }

    public static long getSelectedAccount(Context ctx) {
        return sp(ctx).getInt(PREF_SELECTED_ACCOUNT, 0);
    }

    public static void saveSelectedFragment(Context ctx, int whichFragment) {
        sp(ctx).edit().putInt(PREF_SELECTED_FRAGMENT, whichFragment).commit();
    }

    public static long getSelectedAccountBalance(Context ctx) {
        return sp(ctx).getLong(PREF_SELECTED_ACCOUNT_BALANCE, 0);
    }

    public static boolean shouldUpdateAccountBalance(Context ctx) {
        long lastUpdateTime = sp(ctx).getLong(PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED, 0);
        return System.currentTimeMillis() - lastUpdateTime > ACCOUNT_UPDATE_INTERVAL_MS;
    }

    public static void saveSelectedAccountBalance(Context ctx, long balance) {
        sp(ctx).edit().putLong(PREF_SELECTED_ACCOUNT_BALANCE, balance).commit();
        sp(ctx).edit().putLong(PREF_SELECTED_ACCOUNT_BALANCE_LAST_CHECKED, System.currentTimeMillis()).commit();
    }

    public static String getSelectedAccountUserName(Context ctx) {
        return sp(ctx).getString(PREF_SELECTED_ACCOUNT_USER_NAME, "username loading...");
    }

    public static boolean shouldUpdateAccountUserName(Context ctx) {
        long lastUpdateTime = sp(ctx).getLong(PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED, 0);
        return System.currentTimeMillis() - lastUpdateTime > ACCOUNT_UPDATE_INTERVAL_MS;
    }

    public static void saveSelectedAccountUserName(Context ctx, String userName) {
        sp(ctx).edit().putString(PREF_SELECTED_ACCOUNT_USER_NAME, userName).commit();
        sp(ctx).edit().putLong(PREF_SELECTED_ACCOUNT_USER_NAME_LAST_CHECKED, System.currentTimeMillis()).commit();
    }
}
