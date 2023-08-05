package com.example.simplecontactbook;

import static com.example.simplecontactbook.Database.ContactBookContract.contactEntry.CONTENT_URI;
import static com.example.simplecontactbook.Database.ContactBookContract.contactEntry.TABLE_NAME;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.simplecontactbook.Database.ContactBookContract;
import com.example.simplecontactbook.Database.DBHelper;

public class ContactBookProvider extends ContentProvider {

    // For UriMatcher
    private static final int CONTACTS = 100;
    private static final int CONTACT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // static initializer
    static {
        sUriMatcher.addURI(ContactBookContract.CONTENT_AUTHORITY, ContactBookContract.PATH_CONTACT, CONTACTS);
        sUriMatcher.addURI(ContactBookContract.CONTENT_AUTHORITY, ContactBookContract.PATH_CONTACT + "/#", CONTACT_ID);
    }

    // Database Helper Object
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get access to underlying database (read-only for query)
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            case CONTACTS:
                cursor = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CONTACT_ID:
                /**
                 // Using selection and selectionArgs
                 // URI: content://<authority>/tasks/#
                 String id=uri.getPathSegments().get(1);

                 // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                 String mSelection="_id=?";
                 String[] mSelectionArgs=new String[]{id};
                 */
                selection = ContactBookContract.contactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }

        // COMPLETED (4) Set a notification URI on the Cursor and return that Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Get access to the task database (to write new data to)
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        final int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case CONTACTS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = database.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // COMPLETED (5) Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // COMPLETED (1) Get access to the database and write URI matching code to recognize a single item
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // COMPLETED (2) Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the directory case, recognized by the URI path
            case CONTACTS:
                tasksDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            // Handle the single item case, recognized by the ID included in the URI path
            case CONTACT_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = database.delete(TABLE_NAME, "_id=?", new String[]{id});
                /* Or
                // selection = ContactBookContract.contactEntry._ID + "=?";
                // selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // tasksDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                */
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // COMPLETED (3) Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdated;
        switch (match) {
            case CONTACTS:
                rowUpdated = database.update(TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case CONTACT_ID:
                selection = ContactBookContract.contactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = database.update(TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for this " + uri);
        }
        // notification uri for Cursor Loader
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //return updateContacts(uri, contentValues, selection, selectionArgs);
        return rowUpdated;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
