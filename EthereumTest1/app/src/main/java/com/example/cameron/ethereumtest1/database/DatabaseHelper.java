package com.example.cameron.ethereumtest1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by cameron on 10/26/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    //Used for upgrading the database
    private static final int DATABASE_VERSION = 10;

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
    public static final int TX_ACTION_ID_CREATE_PUBLICATION= 5;

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
    public static final String KEY_USER_CONTENT_INDEX = "KEY_USER_CONTENT_INDEX";
    public static final String KEY_CONTENT_IPFS = "KEY_PRIMARY_CONTENT_IPFS";
    public static final String KEY_IMAGE_IPFS = "KEY_PRIMARY_IMAGE_IPFS";
    public static final String KEY_JSON = "KEY_JSON";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_PRIMARY_TEXT = "KEY_PRIMARY_TEXT";
    public static final String KEY_PUBLISHED_DATE= "KEY_PUBLISHED_DATE";
    private static final String KEY_DRAFT = "KEY_DRAFT";

    public static final String TABLE_USER_CONTENT = "table_user_content";
    public static final String CREATE_TABLE_USER_CONTENT = "CREATE TABLE " + TABLE_USER_CONTENT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PUBLISHED_BY_ETH_ADDRESS + " TEXT,"
            + KEY_USER_CONTENT_INDEX + " INTEGER,"
            + KEY_CONTENT_IPFS + " TEXT,"
            + KEY_IMAGE_IPFS + " TEXT,"
            + KEY_JSON + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_PRIMARY_TEXT + " TEXT,"
            + KEY_PUBLISHED_DATE +  " TEXT,"
            + KEY_CONFIRMED + " BOOLEAN,"
            + KEY_DRAFT + " BOOLEAN,"
            + "UNIQUE(" + KEY_PUBLISHED_BY_ETH_ADDRESS + " , " + KEY_DRAFT + ", " + KEY_USER_CONTENT_INDEX + ") ON CONFLICT REPLACE"
            + ")";

    //Publication Content Table
    public static final String KEY_PUBLICATION_INDEX = "KEY_PUBLICATION_INDEX";
    public static final String KEY_PUBLICATION_CONTENT_INDEX = "KEY_USER_CONTENT_INDEX";

    public static final String TABLE_PUBLICATION_CONTENT = "table_publication_content";
    public static final String CREATE_TABLE_PUBLICATION_CONTENT = "CREATE TABLE " + TABLE_PUBLICATION_CONTENT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PUBLICATION_INDEX + " INTEGER,"
            + KEY_PUBLICATION_CONTENT_INDEX + " INTEGER,"
            + KEY_PUBLISHED_BY_ETH_ADDRESS + " INTEGER,"
            + KEY_CONTENT_IPFS + " TEXT,"
            + KEY_IMAGE_IPFS + " TEXT,"
            + KEY_JSON + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_PRIMARY_TEXT + " TEXT,"
            + KEY_PUBLISHED_DATE +  " TEXT,"
            + "UNIQUE(" + KEY_PUBLICATION_INDEX + " , " + KEY_PUBLICATION_CONTENT_INDEX + ") ON CONFLICT REPLACE"
            + ")";

    //Publications Table
    public static final String KEY_PUBLICATION_ID = "KEY_PUBLICATION_ID";
    public static final String KEY_PUBLICATION_NAME = "KEY_PUBLICATION_NAME";
    public static final String KEY_PUBLICATION_META_DATA = "KEY_PUBLICATION_META_DATA";
    public static final String KEY_PUBLICATION_ADMIN_ADDRESS= "KEY_PUBLICATION_ADMIN_ADDRESS";
    //public static final String KEY_NUM_ACCESS_LIST_ADDRESSES = "KEY_PUBLICATION_ADMIN_ADDRESS";
    public static final String KEY_PUBLICATION_NUM_PUBLISHED = "KEY_PUBLICATION_NUM_PUBLISHED";
    public static final String KEY_PUBLICATION_MIN_SUPPORT_COST_WEI = "KEY_PUBLICATION_MIN_SUPPORT_COST_WEI";
    public static final String KEY_PUBLICATION_ADMIN_PAYMENT_PERCENTAGE = "KEY_PUBLICATION_ADMIN_PAYMENT_PERCENTAGE";
    public static final String KEY_PUBLICATION_UNIQUE_SUPPORTERS = "KEY_PUBLICATION_UNIQUE_SUPPORTERS";
    public static final String KEY_PUBLICATION_SUBSCRIBED_LOCALLY = "KEY_PUBLICATION_SUBSCRIBED_LOCALLY";

    public static final String TABLE_PUBLICATIONS = "table_publications";
    public static final String CREATE_TABLE_PUBLICATIONS = "CREATE TABLE " + TABLE_PUBLICATIONS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_PUBLICATION_ID + " INTEGER,"
            + KEY_PUBLICATION_NAME + " TEXT,"
            + KEY_PUBLICATION_META_DATA + " TEXT,"
            + KEY_PUBLICATION_ADMIN_ADDRESS + " TEXT,"
            + KEY_PUBLICATION_NUM_PUBLISHED + " INTEGER,"
            + KEY_PUBLICATION_MIN_SUPPORT_COST_WEI + " INTEGER,"
            + KEY_PUBLICATION_ADMIN_PAYMENT_PERCENTAGE + " INTEGER,"
            + KEY_PUBLICATION_UNIQUE_SUPPORTERS + " INTEGER,"
            + KEY_PUBLICATION_SUBSCRIBED_LOCALLY + " BOOLEAN,"
            + "UNIQUE(" + KEY_PUBLICATION_ID + ") ON CONFLICT IGNORE"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER_CONTENT);
        db.execSQL(CREATE_TABLE_PUBLICATION_CONTENT);
        db.execSQL(CREATE_TABLE_PUBLICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ETH_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLICATION_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLICATIONS);
        db.execSQL(CREATE_TABLE_ETH_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_USER_CONTENT);
        db.execSQL(CREATE_TABLE_PUBLICATION_CONTENT);
        db.execSQL(CREATE_TABLE_PUBLICATIONS);
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

    public void saveUserContentItem(DBUserContentItem userContentItem) {
        ArrayList<DBUserContentItem> items = new ArrayList<>();
        items.add(userContentItem);
        saveUserContentItems(items);
    }

    public void saveUserContentItems(List<DBUserContentItem> userContentItems) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (DBUserContentItem userContent: userContentItems) {
            ContentValues values = new ContentValues(8);
            values.put(KEY_PUBLISHED_BY_ETH_ADDRESS, userContent.publishedByEthAddress);
            values.put(KEY_USER_CONTENT_INDEX, userContent.userContentIndex);
            values.put(KEY_CONTENT_IPFS, userContent.contentIPFS);
            values.put(KEY_IMAGE_IPFS, userContent.imageIPFS);
            values.put(KEY_JSON, userContent.json);
            values.put(KEY_TITLE, userContent.title);
            values.put(KEY_PRIMARY_TEXT, userContent.primaryText);
            values.put(KEY_PUBLISHED_DATE, userContent.publishedDate);
            values.put(KEY_CONFIRMED, userContent.confirmed);
            values.put(KEY_DRAFT, userContent.draft);
            db.insertWithOnConflict(TABLE_USER_CONTENT, null, values, CONFLICT_REPLACE);
        }
        db.close();
    }

    public void savePublicationContentItems(List<DBPublicationContentItem> publicationContentItems) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (DBPublicationContentItem publicationContent: publicationContentItems) {
            ContentValues values = new ContentValues(8);
            values.put(KEY_PUBLICATION_INDEX, publicationContent.publicationIndex);
            values.put(KEY_USER_CONTENT_INDEX, publicationContent.publicationContentIndex);
            values.put(KEY_PUBLISHED_BY_ETH_ADDRESS, publicationContent.publishedByEthAddress);
            values.put(KEY_CONTENT_IPFS, publicationContent.contentIPFS);
            values.put(KEY_IMAGE_IPFS, publicationContent.imageIPFS);
            values.put(KEY_JSON, publicationContent.json);
            values.put(KEY_TITLE, publicationContent.title);
            values.put(KEY_PRIMARY_TEXT, publicationContent.primaryText);
            values.put(KEY_PUBLISHED_DATE, publicationContent.publishedDate);
            db.insertWithOnConflict(TABLE_PUBLICATION_CONTENT, null, values, CONFLICT_IGNORE);
        }
        db.close();
    }



    public void deleteUserItem(String user, int itemIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        String WHERE = KEY_PUBLISHED_BY_ETH_ADDRESS + "=? and "
                + KEY_DRAFT + "=? and "
                + KEY_USER_CONTENT_INDEX + "=?";
        String[] WHERE_ARGS = new String[] {user, "1", String.valueOf(itemIndex)};
        db.delete(TABLE_USER_CONTENT, WHERE, WHERE_ARGS);
        db.close();
    }



    public void savePublications(ArrayList<DBPublication> dbSaveList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (DBPublication pub: dbSaveList) {
            ContentValues values = new ContentValues(8);
            values.put(KEY_PUBLICATION_ID, pub.publicationID);
            values.put(KEY_PUBLICATION_NAME, pub.name);
            values.put(KEY_PUBLICATION_META_DATA, pub.metaData);
            values.put(KEY_PUBLICATION_ADMIN_ADDRESS, pub.adminAddress);
            values.put(KEY_PUBLICATION_NUM_PUBLISHED, pub.numPublished);
            values.put(KEY_PUBLICATION_MIN_SUPPORT_COST_WEI, pub.minSupportCostWei);
            values.put(KEY_PUBLICATION_ADMIN_PAYMENT_PERCENTAGE, pub.adminPaymentPercentage);
            values.put(KEY_PUBLICATION_UNIQUE_SUPPORTERS, pub.uniqueSupporters);
            values.put(KEY_PUBLICATION_SUBSCRIBED_LOCALLY, pub.subscribedLocally);

            db.insertWithOnConflict(TABLE_PUBLICATIONS, null, values, CONFLICT_REPLACE);
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



    public int getNumContentItemsForUser(Context context, String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        String WHERE = " WHERE " + KEY_PUBLISHED_BY_ETH_ADDRESS + "= \"" + address + "\"";

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER_CONTENT + WHERE, null);
        return c.getCount();
    }

    public Cursor getPublicationContentCursor(int publicationIndex, int quantity) {
        String ORDER_BY = " ORDER BY " + KEY_PUBLICATION_CONTENT_INDEX + " DESC";

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PUBLICATION_CONTENT + " WHERE " + KEY_PUBLICATION_INDEX +  " = " + "\"" + publicationIndex + "\"" + ORDER_BY, null);
    }

    public Cursor getPublicationsCursor() {
        String ORDER_BY = " ORDER BY " + KEY_PUBLICATION_ID + " ASC";

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PUBLICATIONS + ORDER_BY, null);

    }

    public Cursor getPublicationsToViewCursor() {
        String ORDER_BY = " ORDER BY " + KEY_PUBLICATION_ID + " ASC";
        String WHERE = ""; //" WHERE " + KEY_PUBLICATION_NUM_PUBLISHED + " > 0";

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PUBLICATIONS + WHERE + ORDER_BY, null);

    }

    public Cursor getPublicationsOfAddressCursor(String address) {
        String ORDER_BY = " ORDER BY " + KEY_PUBLICATION_ID + " ASC";
        String WHERE = " WHERE " + KEY_PUBLICATION_ADMIN_ADDRESS + "= \"" + address + "\"";

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PUBLICATIONS + WHERE + ORDER_BY, null);

    }

    public Cursor getPublicationsWeCanPublishToCursor(String address) {
        String ORDER_BY = " ORDER BY " + KEY_PUBLICATION_ID + " ASC";
        String WHERE = " WHERE " + KEY_PUBLICATION_ADMIN_ADDRESS + "= \"" + address + "\"";
        WHERE += " OR " + KEY_PUBLICATION_ID + " = 0";

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PUBLICATIONS + WHERE + ORDER_BY, null);

    }

    public static DBUserContentItem convertCursorToDBUserContentItem(Cursor c) {
        String address = c.getString(c.getColumnIndex(KEY_PUBLISHED_BY_ETH_ADDRESS));
        int userContentIndex = c.getInt(c.getColumnIndex(KEY_USER_CONTENT_INDEX));
        String contentIPFS = c.getString(c.getColumnIndex(KEY_CONTENT_IPFS));
        String imageIPFS = c.getString(c.getColumnIndex(KEY_IMAGE_IPFS));
        String json = c.getString(c.getColumnIndex(KEY_JSON));
        String title = c.getString(c.getColumnIndex(KEY_TITLE));
        String primaryText = c.getString(c.getColumnIndex(KEY_PRIMARY_TEXT));
        long publishedDate = c.getLong(c.getColumnIndex(KEY_PUBLISHED_DATE));
        boolean confirmed = c.getInt(c.getColumnIndex(KEY_CONFIRMED)) == 1;
        boolean draft = c.getInt(c.getColumnIndex(KEY_DRAFT)) == 1;

        return new DBUserContentItem(address, userContentIndex, contentIPFS, imageIPFS, json, title,
                primaryText, publishedDate, confirmed, draft);
    }

    public static DBPublicationContentItem convertCursorToDBPublicationContentItem(Cursor c) {
        int publicationIndex = c.getInt(c.getColumnIndex(KEY_PUBLICATION_INDEX));
        int publicationUserContentIndex = c.getInt(c.getColumnIndex(KEY_PUBLICATION_CONTENT_INDEX));
        String address = c.getString(c.getColumnIndex(KEY_PUBLISHED_BY_ETH_ADDRESS));
        String contentIPFS = c.getString(c.getColumnIndex(KEY_CONTENT_IPFS));
        String imageIPFS = c.getString(c.getColumnIndex(KEY_IMAGE_IPFS));
        String json = c.getString(c.getColumnIndex(KEY_JSON));
        String title = c.getString(c.getColumnIndex(KEY_TITLE));
        String primaryText = c.getString(c.getColumnIndex(KEY_PRIMARY_TEXT));
        long publishedDate = c.getLong(c.getColumnIndex(KEY_PUBLISHED_DATE));

        return new DBPublicationContentItem(publicationIndex, publicationUserContentIndex, address,
                contentIPFS, imageIPFS, json, title, primaryText, publishedDate);
    }

    public static DBPublication convertCursorToDBPublication(Cursor c) {
        int publicationID = c.getInt(c.getColumnIndex(KEY_PUBLICATION_ID));
        String name = c.getString(c.getColumnIndex(KEY_PUBLICATION_NAME));
        String metaData = c.getString(c.getColumnIndex(KEY_PUBLICATION_META_DATA));
        String adminAddress = c.getString(c.getColumnIndex(KEY_PUBLICATION_ADMIN_ADDRESS));
        int numPublished = c.getInt(c.getColumnIndex(KEY_PUBLICATION_NUM_PUBLISHED));
        int minSupportCost = c.getInt(c.getColumnIndex(KEY_PUBLICATION_MIN_SUPPORT_COST_WEI));
        int adminPayPercentage = c.getInt(c.getColumnIndex(KEY_PUBLICATION_ADMIN_PAYMENT_PERCENTAGE));
        int uniqueSupporters = c.getInt(c.getColumnIndex(KEY_PUBLICATION_UNIQUE_SUPPORTERS));
        boolean subscribed = c.getInt(c.getColumnIndex(KEY_PUBLICATION_SUBSCRIBED_LOCALLY)) == 1;

        return new DBPublication(publicationID, name, metaData, adminAddress, numPublished,
                minSupportCost, adminPayPercentage, uniqueSupporters, subscribed);
    }

    public ArrayList<DBPublication> getPublicationsWeCanPublishTo(String selectedAddress) {
        ArrayList<DBPublication> pubs = new ArrayList<>();
        Cursor c = getPublicationsWeCanPublishToCursor(selectedAddress);
        while (c.moveToNext()) {
            pubs.add(convertCursorToDBPublication(c));
        }
        return pubs;
    }

    public ArrayList<DBPublication> getPublicationsToView() {
        ArrayList<DBPublication> pubs = new ArrayList<>();
        Cursor c = getPublicationsToViewCursor();
        while (c.moveToNext()) {
            pubs.add(convertCursorToDBPublication(c));
        }
        return pubs;
    }
}
