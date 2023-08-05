package com.example.simplecontactbook;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.simplecontactbook.Database.ContactBookContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final long MAX_IMAGE_SIZE_BYTES = 500000; // Maximum allowed image size in bytes (500 KB)
    private Bitmap bitmap;
    CircleImageView imageView;
    FloatingActionButton imageButton;

    TextInputLayout nameTextInputLayout, contactNoTextInputLayout, emailTextInputLayout;
    FloatingActionButton floatingActionButton;
    /**
     * Identifier  for the contact data loader
     */
    private static final int EXISTING_CONTACT_LOADER = 0;
    Button submitButton;
    /**
     * Content Uri for the existing contact (null if it is a new contact)
     */
    private Uri mCurrentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageButton = findViewById(R.id.imageButton);
        nameTextInputLayout = findViewById(R.id.name_text);
        contactNoTextInputLayout = findViewById(R.id.contact_text);
        emailTextInputLayout = findViewById(R.id.email_text);
        floatingActionButton = findViewById(R.id.f_btn);
        submitButton = findViewById(R.id.submit_btn);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we are creating a new contact or editing an existing one
        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        // If the intent DOES NOT contain a contact content URI, then we know that we
        // creating a new contact
        if (mCurrentUri != null) {
            floatingActionButton.setVisibility(View.INVISIBLE);
            submitButton.setText(getString(R.string.update_to_sqlite));
            // Initialize a loader to read the contact data from the database
            // and display the current values in the editor
            //getLoaderManager().initLoader(EXISTING_CONTACT_LOADER,null,null);
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(EXISTING_CONTACT_LOADER, null, this);
        }

        imageButton.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int newId = view.getId();
        if (newId == R.id.submit_btn) {
            insertContact();
            if (mCurrentUri != null) {
                finish();
            }
        } else if (newId == R.id.f_btn) {
            startActivity(new Intent(MainActivity.this, ShowContactBook.class));

        } else if (newId == R.id.imageButton) {
            // Check for API level 23 or higher (Marshmallow and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Check if the WRITE_EXTERNAL_STORAGE permission is not granted
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the WRITE_EXTERNAL_STORAGE permission with request code 1
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                } else {
                    openImageChooser();
                }
            } else {
                openImageChooser();
            }
        }
    }

    // This method is automatically called when the app requests a runtime permission (in this case, gallery permission).
    // It handles the result of the permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the request code matches the GALLERY_REQUEST_CODE.
        if (requestCode == GALLERY_REQUEST_CODE) {
            // Check if the grantResults array is not empty and contains the result for the requested permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, the app can now access the gallery.
                // You can proceed with the action that required gallery permission, in this case, calling the openImageChooser() method.
                openImageChooser();
            } else {
                // Permission denied, the app cannot access the gallery.
                // You may show a message to the user or handle the situation accordingly.
                Toast.makeText(this, "Gallery permission denied. Unable to make the gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // This method is used to open the image chooser dialog, allowing the user to select an image from the gallery.
    private void openImageChooser() {
        // Create an intent to pick an image from the gallery.
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the activity for result, which will return the selected image data to the onActivityResult method.
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    // onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is successful (i.e., an image has been selected or captured).
        if (resultCode == Activity.RESULT_OK) {
            // Check if the request code matches the GALLERY_REQUEST_CODE.
            if (requestCode == GALLERY_REQUEST_CODE) {
                // Check if the data returned from the intent is not null.
                if (data != null) {
                    // Get the Uri of the selected image.
                    Uri selectedImageUri = data.getData();
                    try {
                        // Check the size of the selected image.
                        long imageSize = getImageSize(selectedImageUri);

                        // Compare the image size with the maximum allowed size.
                        if (imageSize > MAX_IMAGE_SIZE_BYTES) {
                            // Show an error message to the user.
                            Toast.makeText(this, "Please select an image up to 500 KB in size.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Convert the Uri to a Bitmap so that it can be displayed in an ImageView.
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Method to get the size of an image given its Uri
    private long getImageSize(Uri uri) {
        long size = 0;
        try {
            // Use a Cursor to query the content resolver for information about the image.
            // The content resolver provides access to the media content (e.g., images) on the device.
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            // Check if the cursor is not null and if it contains any data.
            if (cursor != null && cursor.moveToFirst()) {
                // Get the column index of the image size. The OpenableColumns.SIZE constant
                // represents the size of the data associated with the Uri.
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                // Check if the size index is valid (-1 indicates that the column is not present).
                if (sizeIndex != -1) {
                    // Retrieve the image size from the cursor based on the size index.
                    size = cursor.getLong(sizeIndex);
                }
            }

            // Close the cursor to release any resources associated with it.
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            // Catch any exceptions that might occur during the query or size retrieval.
            e.printStackTrace();
        }

        // Return the image size in bytes.
        return size;
    }

    private void insertContact() {

        byte[] byteArray;

        // Check if the ImageView is not null and has a drawable set
        if (imageView != null && imageView.getDrawable() != null) {
            // Get the Bitmap from the ImageView (if it's already set)
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            bitmap = drawable.getBitmap();
        } else {
            // If the ImageView is not set or doesn't have a drawable, use a default drawable
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile); // Replace "profile" with the name of your default drawable image
        }

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();


        String name = nameTextInputLayout.getEditText().getText().toString();
        String contactNo = contactNoTextInputLayout.getEditText().getText().toString();
        String email = emailTextInputLayout.getEditText().getText().toString();


        // For input Validation
        if (name.isEmpty()) {
            nameTextInputLayout.setError("Please Enter Name");
            nameTextInputLayout.requestFocus();
            return;
        } else {
            nameTextInputLayout.setErrorEnabled(false);
        }

        // Phone number validation
        // Regular expression pattern for validating Pakistan phone numbers
        // This pattern covers various formats of phone numbers used in Pakistan.
        // Explanation:
        // ^: Start of the string.
        // (\\\\+92|92|0)?: Optional group to match the country code. It can be either "+92", "92", or "0" (the leading zero used within Pakistan).
        // (3[0-9]{9}|[1-9][0-9]{9}): Alternation group to match either mobile numbers or landline numbers:
        // - 3[0-9]{9}: Matches a mobile number starting with "3" followed by 9 digits.
        // - [1-9][0-9]{9}: Matches a landline number starting with a non-zero digit followed by 9 digits.
        // $: End of the string.

        String phoneRegex = "^(\\\\+92|92|0)?(3[0-9]{9}|[1-9][0-9]{9})$";
        if (contactNo.isEmpty() || !contactNo.matches(phoneRegex)) {
            contactNoTextInputLayout.setError("Invalid Number! Please enter correct name.");
            contactNoTextInputLayout.requestFocus();
            return;
        } else {
            contactNoTextInputLayout.setErrorEnabled(false);
        }

        // Email validation
        //String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError("Invalid Email! Please enter valid email.");
            emailTextInputLayout.requestFocus();
            return;
        } else {
            emailTextInputLayout.setErrorEnabled(false);
        }

        ContentValues values = new ContentValues();
        values.put(ContactBookContract.contactEntry.COLUMN_CONTACT_NAME, name);
        values.put(ContactBookContract.contactEntry.COLUMN_CONTACT_PH, contactNo);
        values.put(ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE, byteArray);


        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(ContactBookContract.contactEntry.CONTENT_URI, values);
            // SHow a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null , then there was an error with insertion
                Toast.makeText(this, "Insertion Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insertion Successful", Toast.LENGTH_SHORT).show();
                nameTextInputLayout.getEditText().setText("");
                contactNoTextInputLayout.getEditText().setText("");
                emailTextInputLayout.getEditText().setText("");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
                imageView.setImageBitmap(bitmap);
                nameTextInputLayout.requestFocus();
            }
        } else {
            // Otherwise this is an Existing contact , so update the contact with content URI: mCurrentUri
            // and pass in the new ContentValues, Pass in null for the selection and selection args
            // because mCurrentUri will already identify the correct row in the database that
            // we want to notify

            int rowAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful
            if (rowAffected == 0) {
                // If no row are affected, then there was an error with the update
                Toast.makeText(this, "Updating contact Failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast
                Toast.makeText(this, "Updated contact Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Since the editor shows all contact attributes , define a projection that contain
        // all columns from the contact table
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
            int numberColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_PH);
            int emailColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE);


            // Extract out the value from the Cursor for the given Column index
            String name = cursor.getString(nameColumnIndex);
            String ph = cursor.getString(numberColumnIndex);
            String email = cursor.getString(emailColumnIndex);

            // Extract the image byte array from the cursor
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);

            // Convert byte array back to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

            // Update the views on the screen with the values from the database
            nameTextInputLayout.getEditText().setText(name);
            contactNoTextInputLayout.getEditText().setText(ph);
            emailTextInputLayout.getEditText().setText(email);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated , clear out all the data from the input
        nameTextInputLayout.getEditText().setText("");
        contactNoTextInputLayout.getEditText().setText("");
        emailTextInputLayout.getEditText().setText("");
        // Reset the image to null
        imageView.setImageBitmap(null);
    }
}