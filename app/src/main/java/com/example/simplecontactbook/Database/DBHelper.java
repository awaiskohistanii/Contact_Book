package com.example.simplecontactbook.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.simplecontactbook.Database.ContactBookContract;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // String SQL_CREATE_CONTACTS_TABLE = "create table tbl_contact ( id integer primary key autoincrement, name text, contact text, email text)";
        // Create a String that contains the SQL statement to create the contact table
        String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactBookContract.contactEntry.TABLE_NAME + " ("
                + ContactBookContract.contactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContactBookContract.contactEntry.COLUMN_CONTACT_NAME + " TEXT, "
                + ContactBookContract.contactEntry.COLUMN_CONTACT_PH + " TEXT, "
                + ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL + " TEXT, "
                + ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE + " BLOB);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
