package com.narwal.parvesh.findunfollowers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parvesh on 01-Dec-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "NFB_USERS_DB";

    private static final String TABLE_NFB_USERS = "NFB_USERS";

    private static final String KEY_ID = "USER_ID";
    private static final String KEY_NAME = "USER_NAME";
    private static final String KEY_SCREEN_NAME = "SCREEN_NAME";
    private static final String KEY_PROFILE_PIC_URL = "PIC_URL";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NFB_USERS + "("
                + KEY_ID + " INTEGER, "
                + KEY_NAME + " TEXT, "
                + KEY_SCREEN_NAME + " TEXT, "
                + KEY_PROFILE_PIC_URL + " TEXT"
                +")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NFB_USERS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }



    void addNFB_User(NFB_User nfb_user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, nfb_user.getUser_id());
        values.put(KEY_NAME, nfb_user.getUser_name());
        values.put(KEY_SCREEN_NAME, nfb_user.getScreen_name());
        values.put(KEY_PROFILE_PIC_URL, nfb_user.getProfile_pic_url());

        // Inserting Row
        db.insert(TABLE_NFB_USERS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    NFB_User getNFB_User(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        NFB_User nfb_user = null;
        Cursor cursor = db.query(TABLE_NFB_USERS,
                new String[] { KEY_ID, KEY_NAME, KEY_SCREEN_NAME, KEY_PROFILE_PIC_URL },
                KEY_ID + "=" + String.valueOf(id),
                null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount()> 0)
            nfb_user = new NFB_User(Long.parseLong(cursor.getString(0)),cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.close();
        }

        // return contact
        return nfb_user;
    }

    // Getting All Contacts
    public List<NFB_User> getNFB_Users() {
        List<NFB_User> nfb_users = new ArrayList<NFB_User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NFB_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NFB_User nfb_user = new NFB_User();
                nfb_user.setUser_id(Long.parseLong(cursor.getString(0)));
                nfb_user.setUser_name(cursor.getString(1));
                nfb_user.setScreen_name(cursor.getString(2));
                nfb_user.setProfile_pic_url(cursor.getString(3));

                // Adding contact to list
                nfb_users.add(nfb_user);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return nfb_users;
    }

    public void deleteCachedData(){
        SQLiteDatabase db = this.getWritableDatabase();

        if(tableExists(db, TABLE_NFB_USERS)) db.execSQL("delete from "+ TABLE_NFB_USERS);

    }

    public boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }

        else{
            cursor.close();
            return true;
        }

    }


    // Updating single contact
//    public int updateContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }

    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }


    // Getting contacts Count
//    public int getContactsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }
}
