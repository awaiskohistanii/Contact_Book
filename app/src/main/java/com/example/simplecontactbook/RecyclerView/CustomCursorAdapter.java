package com.example.simplecontactbook.RecyclerView;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecontactbook.ContactDetail;
import com.example.simplecontactbook.Database.ContactBookContract;
import com.example.simplecontactbook.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder>{

    // Class variables for the Cursor that holds task data and the Context
    private Cursor cursor;

    private Context mContext;




    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_view, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = cursor.getColumnIndex(ContactBookContract.contactEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_NAME);
        int phColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_PH);
        int emailColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_EMAIL);
        int imageColumnIndex = cursor.getColumnIndex(ContactBookContract.contactEntry.COLUMN_CONTACT_IMAGE);

        cursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameColumnIndex);
        String ph = cursor.getString(phColumnIndex);
        String email = cursor.getString(emailColumnIndex);

        // Extract the image byte array from the cursor
        byte[] imageByteArray = cursor.getBlob(imageColumnIndex);

        // Convert byte array back to Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);


        //Set values
        holder.itemView.setTag(id);
        holder.nameTextView.setText(name);
        holder.circleImageView.setImageBitmap(bitmap);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //[Hint] Use getTag (from the adapter code) to get the id
                // Retrieve the id of the task to delete
                int id = (int) holder.itemView.getTag();
                // Build the URI directly without converting the id to a string
                Uri uri = ContentUris.withAppendedId(ContactBookContract.contactEntry.CONTENT_URI, id);
                // Show Toast message
                Toast.makeText(mContext, "" + uri.toString() + " : " + name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ContactDetail.class);
                // setting the URI on the data field of the intent
                intent.setData(uri);
                mContext.startActivity(intent);

                // For hover animation
                applyTemporaryHoverEffect(holder.itemView);
            }
        });
    }

    private void applyTemporaryHoverEffect(View view) {
        final int originalBackgroundColor = view.getSolidColor();
        final int hoverColor = ContextCompat.getColor(mContext, R.color.hoverColor);
        final int duration = 100; // Duration of the hover effect in milliseconds
        view.setBackgroundColor(hoverColor);
        // Reset the background color after a short duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(originalBackgroundColor);
            }
        }, duration);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;

    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {


        // Class variables for the task description and priority TextViews
        TextView nameTextView;
        CircleImageView circleImageView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.list_imageView);
        }
    }
}
