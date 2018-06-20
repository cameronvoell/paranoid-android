package com.example.cameron.ethereumtest1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cameron.ethereumtest1.model.ContentItem;
import com.example.cameron.ethereumtest1.model.EthereumTransaction;
import com.example.cameron.ethereumtest1.model.UserFragmentContentItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by cameron on 10/26/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    //Used for upgrading the database
    private static final int DATABASE_VERSION = 1;

    //name of our database file
    private static final String DATABASE_NAME = "digital_desert_database";

    //Shared Database Column names
    public static final String KEY_ID = "_id";

//    //User Content Database
//    public static final String KEY_PUBLISHED_BY_ADDRESS = "published_by_address";
//    public static final String KEY_CONTENT_INDEX = "content_index";
//    public static final String KEY_CONTENT_HASH = "content_hash";
//    public static final String KEY_LINK_1 = "content_link_1";
//    public static final String KEY_LINK_2 = "content_link_2";
//    public static final String KEY_PUBLISHED_DATE = "published_date";
//    public static final String KEY_CONTENT_TITLE = "content_title";
//    public static final String KEY_PRIMARY_TEXT = "primary_text";
//    public static final String KEY_PRIMARY_IMAGE_URL = "primary_image_url";
//    public static final String KEY_PRIMARY_HTTP_LINK = "primary_http_link";
//    public static final String KEY_PRIMARY_CONTENT_ADDRESSED_LINK = "primary_content_addressed_link";
//
//    public static final String TABLE_USER_CONTENT = "table_user_content";
//    public static final String CREATE_TABLE_USER_CONTENT = "CREATE TABLE " + TABLE_USER_CONTENT
//            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
//            + KEY_PUBLISHED_BY_ADDRESS + " TEXT, "
//            + KEY_CONTENT_INDEX + " INTEGER, "
//            + KEY_CONTENT_HASH + " TEXT, "
//            + KEY_LINK_1 + " TEXT, "
//            + KEY_LINK_2 + " TEXT, "
//            + KEY_PUBLISHED_DATE + " INTEGER, "
//            + KEY_CONTENT_TITLE + " TEXT, "
//            + KEY_PRIMARY_TEXT + " TEXT, "
//            + KEY_PRIMARY_IMAGE_URL + " TEXT, "
//            + KEY_PRIMARY_HTTP_LINK + " TEXT, "
//            + KEY_PRIMARY_CONTENT_ADDRESSED_LINK + " TEXT)";

    //Ethereum Transactions Table
    public static final String KEY_ETH_ADDRESS = "eth_address_from";
    public static final String KEY_ETH_TX_ID = "eth_tx_id";
    public static final String KEY_TX_ACTION_ID = "tx_action_id";
    public static final String KEY_TX_CONTENT = "tx_content";
    public static final String KEY_TX_TIMESTAMP = "tx_timestamp";
    public static final String KEY_BLOCK_NUMBER = "block_number";
    public static final String KEY_CONFIRMED = "confirmed";
    public static final String KEY_GAS_COST = "gas_cost";

    public static final int TX_ACTION_ID_REGISTER = 0;
    public static final int TX_ACTION_ID_UPDATE_PROFILE_META_DATA = 1;
    public static final int TX_ACTION_ID_POST_USER_CONTENT = 2;
    public static final int TX_ACTION_ID_SEND_ETH = 3;

    public static final String TABLE_ETH_TRANSACTIONS = "table_eth_transactions";
    public static final String CREATE_TABLE_ETH_TRANSACTIONS = "CREATE TABLE " + TABLE_ETH_TRANSACTIONS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_ETH_ADDRESS + " TEXT,"
            + KEY_ETH_TX_ID +  " TEXT,"
            + KEY_TX_ACTION_ID + " INTEGER,"
            + KEY_TX_CONTENT + " TEXT,"
            + KEY_TX_TIMESTAMP + " TEXT,"
            + KEY_BLOCK_NUMBER + " INTEGER,"
            + KEY_CONFIRMED + " BOOLEAN,"
            + KEY_GAS_COST + " INTEGER,"
            + "UNIQUE(" + KEY_ETH_TX_ID + ") ON CONFLICT REPLACE"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE_USER_CONTENT);
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ETH_TRANSACTIONS);
//        db.execSQL(CREATE_TABLE_USER_CONTENT);
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
    }

    public void saveTransactionInfo(EthereumTransaction tx) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues(8);
        values.put(KEY_ETH_ADDRESS, tx.ethAddress);
        values.put(KEY_ETH_TX_ID, tx.ethTxId);
        values.put(KEY_TX_ACTION_ID, tx.txActionId);
        values.put(KEY_TX_CONTENT, tx.txContent);
        values.put(KEY_TX_TIMESTAMP, tx.txTimestamp);
        values.put(KEY_BLOCK_NUMBER, tx.blockNumber);
        values.put(KEY_CONFIRMED, tx.confirmed);
        values.put(KEY_GAS_COST, tx.gasCost);
        db.insertWithOnConflict(TABLE_ETH_TRANSACTIONS, null, values, CONFLICT_REPLACE);
        db.close();
    }

    public Cursor getTransactionCursor(String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ETH_TRANSACTIONS + " WHERE " + KEY_ETH_ADDRESS +  " = " + "\"" + address + "\"", null);
    }

//    public List<UserFragmentContentItem> getUserFragmentContentItems(String address, int startId, int endId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER_CONTENT, null);
//        ArrayList<UserFragmentContentItem> contentItems = new ArrayList<>();
//        while (c.moveToNext()) {
//            contentItems.add(cursorToUserFragmentContentItem(c));
//        }
//        return contentItems;
//    }

//    private UserFragmentContentItem cursorToUserFragmentContentItem(Cursor c) {
//        ContentItem ci = new ContentItem(c.getString(c.getColumnIndex(KEY_PUBLISHED_BY_ADDRESS)),
//                c.getString(c.getColumnIndex(KEY_CONTENT_TITLE)),
//                c.getLong(c.getColumnIndex(KEY_PUBLISHED_DATE)),
//                c.getString(c.getColumnIndex(KEY_PRIMARY_TEXT)),
//                c.getString(c.getColumnIndex(KEY_PRIMARY_IMAGE_URL)),
//                c.getString(c.getColumnIndex(KEY_PRIMARY_HTTP_LINK)),
//                c.getString(c.getColumnIndex(KEY_PRIMARY_CONTENT_ADDRESSED_LINK))
//                );
//        UserFragmentContentItem ufci = new UserFragmentContentItem(c.getLong(c.getColumnIndex(KEY_CONTENT_INDEX)), ci,
//                c.getString(c.getColumnIndex(KEY_CONTENT_HASH)),
//                c.getString(c.getColumnIndex(KEY_LINK_1)),
//                c.getString(c.getColumnIndex(KEY_LINK_2)));
//        return ufci;
//    }

//    public void addUserFragmentContentItems(List<UserFragmentContentItem> items) {
//        SQLiteDatabase db = this.getWritableDatabase();

//        for (UserFragmentContentItem item: items) {
//            ContentValues values = new ContentValues();
//            values.put(KEY_PUBLISHED_BY_ADDRESS, item.contentItem.publishedBy);
//            values.put(KEY_CONTENT_INDEX, item.index);
//            values.put(KEY_CONTENT_HASH, item.contentHash);
//            values.put(KEY_LINK_1, item.link1);
//            values.put(KEY_LINK_2, item.link2);
//            values.put(KEY_PUBLISHED_DATE, item.contentItem.publishedDate);
//            values.put(KEY_CONTENT_TITLE, item.contentItem.title);
//            values.put(KEY_PRIMARY_TEXT, item.contentItem.primaryText);
//            values.put(KEY_PRIMARY_IMAGE_URL, item.contentItem.primaryImageUrl);
//            values.put(KEY_PRIMARY_HTTP_LINK, item.contentItem.primaryHttpLink);
//            values.put(KEY_PRIMARY_CONTENT_ADDRESSED_LINK, item.contentItem.primaryContentAddressedLink);

//            db.insertWithOnConflict(TABLE_USER_CONTENT, null, values, CONFLICT_REPLACE);
//        }
//    }

}
