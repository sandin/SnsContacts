package com.lds.snscontacts.db;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ContactsDatabase {
    
    public static final String DB_NAME = "sns_contacts.db";
    public static final int DB_VERSION = 1;
    
    
    public static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    
    private MySQLiteOpenHelper mOpenHelper;
    
    private static ContactsDatabase mInstance;
    
    public static ContactsDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ContactsDatabase(context);
        }
        return mInstance;
    }
    
    private ContactsDatabase(Context context) {
        mOpenHelper = new MySQLiteOpenHelper(context);
    }
    
    public SQLiteDatabase getDb(boolean writeable) {
        if (writeable) {
            return mOpenHelper.getWritableDatabase();
        } else {
            return mOpenHelper.getReadableDatabase();
        }
    }
    
    // Tables 
    
    public static final String CONTACTS_TABLE_NAME = "contacts";
    
    public interface ContactsCols extends BaseColumns {
        public static final String UID = "uid";
        public static final String SCREEN_NAME = "screen_name";
        public static final String NAME = "name";
        public static final String LOCATION = "location";
        public static final String DESCRIPTION = "description";
        public static final String PROFILE_IMAGE_URL = "profile_image_url";
        public static final String AVATAR_LARGE = "avatar_large";
        public static final String GENDER = "gender";
        public static final String REMARK = "remark";
        public static final String STATUS_TEXT = "status_text";
        public static final String FOLLOW_ME = "follow_me";
    }
    
    private static final String CONTACTS_TABLE_CREATE = "CREATE TABLE "
            + CONTACTS_TABLE_NAME + " ( " 
            + ContactsCols._ID + " INTEGER PRIMARY KEY , "
            + ContactsCols.UID + " TEXT NOT NULL DEFAULT '', "
            + ContactsCols.SCREEN_NAME + " TEXT NOT NULL DEFAULT '', "
            + ContactsCols.NAME + " TEXT NOT NULL DEFAULT '', "
            + ContactsCols.LOCATION + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.DESCRIPTION + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.PROFILE_IMAGE_URL + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.AVATAR_LARGE + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.GENDER + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.REMARK + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.STATUS_TEXT + " TEXT NOT NULL DEFAULT '',"
            + ContactsCols.FOLLOW_ME + " INTEGER NOT NULL DEFAULT 0"
            + " ); ";
    
    private class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public MySQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CONTACTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
    }
    
    public MySQLiteOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

}
