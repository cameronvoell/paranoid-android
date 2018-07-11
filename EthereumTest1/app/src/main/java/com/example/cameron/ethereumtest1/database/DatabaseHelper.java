package com.example.cameron.ethereumtest1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by cameron on 10/26/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    //Used for upgrading the database
    private static final int DATABASE_VERSION = 1;

    //name of our database file
    private static final String DATABASE_NAME = "circus_droid";

    //Shared Database Column names
    public static final String KEY_ID = "_id";
    public static final String KEY_CONFIRMED = "confirmed";

    //Ethereum Transactions Table
    public static final String KEY_ETH_ADDRESS = "eth_address_from";
    public static final String KEY_ETH_TX_ID = "eth_tx_id";
    public static final String KEY_TX_ACTION_ID = "tx_action_id";
    public static final String KEY_TX_CONTENT = "tx_content";
    public static final String KEY_TX_TIMESTAMP = "tx_timestamp";
    public static final String KEY_BLOCK_NUMBER = "block_number";
    public static final String KEY_GAS_COST = "gas_cost";

    public static final int TX_ACTION_ID_SEND_ETH = 0;
    public static final int TX_ACTION_ID_REGISTER = 1;
    public static final int TX_ACTION_ID_UPDATE_USER_PIC = 2;
    public static final int TX_ACTION_ID_PUBLISH_USER_CONTENT= 3;
    public static final int TX_ACTION_ID_PUBLISH_TO_PUBLICATION= 4;

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

    //User Content Table
    public static final String KEY_PUBLISHED_BY_ETH_ADDRESS = "KEY_PUBLISHED_BY_ETH_ADDRESS";
    public static final String KEY_PUBLISHED_DATE= "KEY_PUBLISHED_DATE";
    public static final String KEY_TX_ID = "KEY_TX_ID";
    public static final String KEY_PRIMARY_CONTENT_IPFS = "KEY_PRIMARY_CONTENT_IPFS";
    public static final String KEY_PRIMARY_IMAGE_IPFS = "KEY_PRIMARY_IMAGE_IPFS";
    public static final String KEY_JSON = "KEY_JSON";
    public static final String KEY_USER_CONTENT_INDEX = "KEY_USER_CONTENT_INDEX";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_PRIMARY_TEXT = "KEY_PRIMARY_TEXT";
    public static final String KEY_HTTP_LINK = "KEY_HTTP_LINK";

    public static final String TABLE_USER_CONTENT = "table_user_content";
    public static final String CREATE_TABLE_USER_CONTENT = "CREATE TABLE " + TABLE_USER_CONTENT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PUBLISHED_BY_ETH_ADDRESS + " TEXT,"
            + KEY_PUBLISHED_DATE +  " TEXT,"
            + KEY_TX_ID + " TEXT,"
            + KEY_PRIMARY_CONTENT_IPFS + " TEXT,"
            + KEY_PRIMARY_IMAGE_IPFS + " TEXT,"
            + KEY_JSON + " TEXT,"
            + KEY_USER_CONTENT_INDEX + " INTEGER,"
            + KEY_TITLE + " TEXT,"
            + KEY_PRIMARY_TEXT + " TEXT,"
            + KEY_HTTP_LINK + " TEXT,"
            + KEY_CONFIRMED + " BOOLEAN"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER_CONTENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ETH_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER_CONTENT);
    }

    public void saveTransactionInfo(DBEthereumTransaction tx) {
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

    public void saveUserContentItems(List<DBUserContentItem> userContentItems) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (DBUserContentItem userContent: userContentItems) {
            ContentValues values = new ContentValues(8);
            values.put(KEY_PUBLISHED_BY_ETH_ADDRESS, userContent.publishedByEthAddress);
            values.put(KEY_PUBLISHED_DATE, userContent.publishedDate);
            values.put(KEY_TX_ID, userContent.txId);
            values.put(KEY_PRIMARY_CONTENT_IPFS, userContent.contentIPFS);
            values.put(KEY_PRIMARY_IMAGE_IPFS, userContent.imageIPFS);
            values.put(KEY_JSON, userContent.json);
            values.put(KEY_USER_CONTENT_INDEX, userContent.index);
            values.put(KEY_TITLE, userContent.contentItem.title);
            values.put(KEY_PRIMARY_TEXT, userContent.contentItem.primaryText);
            values.put(KEY_HTTP_LINK, userContent.contentItem.primaryHttpLink);
            values.put(KEY_CONFIRMED, userContent.confirmed);
            db.insertWithOnConflict(TABLE_ETH_TRANSACTIONS, null, values, CONFLICT_IGNORE);
        }
        db.close();
    }

    public Cursor getTransactionCursor(String address) {
        String ORDER_BY = " ORDER BY " + KEY_TX_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ETH_TRANSACTIONS + " WHERE " + KEY_ETH_ADDRESS +  " = " + "\"" + address + "\"" + ORDER_BY, null);
    }

    public Cursor getUserContentCursor(String address, int quantity) {
        String ORDER_BY = " ORDER BY " + KEY_PUBLISHED_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER_CONTENT + " WHERE " + KEY_PUBLISHED_BY_ETH_ADDRESS +  " = " + "\"" + address + "\"" + ORDER_BY, null);
    }

}
