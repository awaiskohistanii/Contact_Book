package com.example.simplecontactbook.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContactBookContract {
    public static final String CONTENT_AUTHORITY="com.example.simplecontactbook";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_CONTACT="contact";

    public static final class contactEntry implements BaseColumns {
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_CONTACT);

        /** Name of database table for contact */
        public final static String TABLE_NAME = "contact";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CONTACT_NAME ="name";
        public final static String COLUMN_CONTACT_PH = "contact_no";
        public final static String COLUMN_CONTACT_EMAIL = "email";
        public final static String COLUMN_CONTACT_IMAGE = "image";
    }
}
