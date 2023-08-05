package com.example.simplecontactbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplecontactbook.Database.ContactBookContract;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int REQUEST_CALL_PERMISSION = 101;
    private static final int REQUEST_SEND_SMS_PERMISSION = 102;
    private Uri mCurrentUri;
    CircleImageView circleImageViewDetail;
    TextView  nameTextViewDetail, phTextViewDetail, emailTextViewDetail;
    ImageButton imageButtonCall, imageButtonMessage, imageButtonWhatsappCall, imageButtonWhatsappMessage, imageButtonEdit;
    private static final int EXISTING_CONTACT_LOADER = 0;
    String ph;
    Bitmap bitmap;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        circleImageViewDetail = findViewById(R.id.detail_imageView);
        nameTextViewDetail = findViewById(R.id.name_detail);
        phTextViewDetail = findViewById(R.id.ph_detail);
        imageButtonCall = findViewById(R.id.call_btn);
        imageButtonMessage = findViewById(R.id.message_btn);
        emailTextViewDetail = findViewById(R.id.email_detail);
        imageButtonEdit = findViewById(R.id.edit_btn);
        imageButtonWhatsappCall = findViewById(R.id.whatsapp_call_btn);
        imageButtonWhatsappMessage = findViewById(R.id.whatsapp_message_btn);
        toolbar = findViewById(R.id.detail_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("");
        }
        // Set the navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageButtonCall.setOnClickListener(this);
        imageButtonMessage.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageButtonWhatsappCall.setOnClickListener(this);
        imageButtonWhatsappMessage.setOnClickListener(this);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        // Initialize a loader to read the contact data from the database
        // and display the current values
        //getLoaderManager().initLoader(contact_LOADER,null,null);
        androidx.loader.app.LoaderManager loaderManager = androidx.loader.app.LoaderManager.getInstance(this);
        loaderManager.initLoader(EXISTING_CONTACT_LOADER, null, this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.call_btn) {
            // Check for API level 23 or higher (Marshmallow and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Check if the CALL_PHONE permission is not granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the CALL_PHONE permission with request code 1
                    ActivityCompat.requestPermissions(ContactDetail.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                } else {
                    makePhoneCall();
                }
            } else {
                makePhoneCall();
            }

        } else if (id == R.id.message_btn) {
            // Check for API level 23 or higher (Marshmallow and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Check if the CALL_PHONE permission is not granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Request the CALL_PHONE permission with request code 1
                    ActivityCompat.requestPermissions(ContactDetail.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
                } else {
                    sendSMS();
                }
            } else {
                sendSMS();
            }

        } else if (id == R.id.edit_btn) {
            Intent intent = new Intent(this, MainActivity.class);
            // setting the URI on the data field of the intent
            intent.setData(mCurrentUri);
            startActivity(intent);

        } else if ((id == R.id.whatsapp_message_btn) || (id == R.id.whatsapp_call_btn)) {
            // Create the WhatsApp intent with the phone number in the data URI
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + ph));

            // Verify that the user's device has WhatsApp installed
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case when WhatsApp is not installed on the device
                // For example, you can display a message to the user or use a different communication method
                Toast.makeText(this, "Please Install WhatsApp", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Handle the permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the phone call
                makePhoneCall();
            } else {
                // Permission denied, display a message or handle the situation accordingly
                Toast.makeText(this, "Call permission denied. Unable to make the call.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SEND_SMS permission granted, send the SMS
                sendSMS();
            } else {
                // Permission denied, display a message or handle the situation accordingly
                Toast.makeText(this, "Send SMS permission denied. Unable to send the SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to make the phone call
    private void makePhoneCall() {
        // ... Your existing code for making the phone call ...
        // Create an Intent for making a phone call using the ACTION_CALL action
        Intent intent = new Intent(Intent.ACTION_CALL);

        // Set the data for the Intent with the phone number (variable 'ph')
        // We want to make a phone call, so we need to specify the phone number as the data
        // The 'Uri.parse()' method is used to convert the phone number (string) into a URI with the "tel:" scheme
        // The "tel:" scheme is a standard way to represent phone numbers in Android
        // For example, if 'ph' contains the phone number "1234567890", the URI will be "tel:1234567890"
        intent.setData(Uri.parse("tel:" + ph));
        startActivity(intent);
    }

    // Method to send the SMS
    private void sendSMS() {
        // ... Your existing code for sending the SMS ...
        // Create an intent to open the SMS app with the recipient's number pre-filled
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + ph));

            /*
            // If you want to add an initial text message, you can use the following line:
            // intent.putExtra("sms_body", "Hello, this is a pre-filled SMS from my Android app!");
            */
        // Verify that the user's device has an app capable of handling the SMS intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case when no SMS app is available on the device
            // For example, you can display a message to the user or use a different communication method
            Toast.makeText(this, "SMS Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that contain all columns from the contact table
        String[] projection = {
                ContactBookContract.contactEntry._ID,
                ContactBookContract.contactEntry.COLUMN_CONTACT_NAME,
                ContactBookContract.contactEntry.COLUMN_CONTACT_PH,
                ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL,
                ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE
        };
        return new CursorLoader(this, // parent activity content
                mCurrentUri, // Query the content URI for the current contact
                projection, // Columns to include in the resulting cursor
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // ( This should be the only row in the cursor
        if (cursor.moveToNext()) {
            // Find the columns of contact attributes that we are interested in
            int nameColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_NAME);
            int breedColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_PH);
            int genderColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE);

            // Extract out the value from the Cursor for the given Column index
            String name = cursor.getString(nameColumnIndex);
            ph = cursor.getString(breedColumnIndex);
            String email = cursor.getString(genderColumnIndex);

            // Extract the image byte array from the cursor
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);

            // Convert byte array back to Bitmap
            bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

            // Display data from database
            nameTextViewDetail.setText(name);
            phTextViewDetail.setText(ph);
            emailTextViewDetail.setText(email);
            circleImageViewDetail.setImageBitmap(bitmap);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated , clear out all the data from the input
        // Reset the image to null
        circleImageViewDetail.setImageBitmap(null);
        nameTextViewDetail.setText("");
        phTextViewDetail.setText("");
        emailTextViewDetail.setText("");
    }

}