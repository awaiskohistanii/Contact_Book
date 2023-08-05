package com.example.simplecontactbook;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.simplecontactbook.Database.ContactBookContract;
import com.example.simplecontactbook.RecyclerView.CustomCursorAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;


public class ShowContactBook extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;

    Toolbar toolbar;

    CollapsingToolbarLayout collapsingToolbarLayout;

    TextView textViewTotalContacts;

    SearchView searchView;
    LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact_book);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        textViewTotalContacts = findViewById(R.id.total_contacts);
        searchView = findViewById(R.id.search);
        lottieAnimationView = findViewById(R.id.lottie_animation);


        setSupportActionBar(toolbar);

        // Change the icon color programmatically
        int color = ContextCompat.getColor(this, R.color.black);
        // Get the back arrow icon drawable
        Drawable icon = AppCompatResources.getDrawable(this, R.drawable.arrow_back_24);
        // Apply color tint to the icon if it is not null
        if (icon != null) {
            // Wrap the icon drawable to make it mutable and allow color changes
            icon = DrawableCompat.wrap(icon);
            // Set the color tint to the icon
            DrawableCompat.setTint(icon, color);
            // Set the customized icon as the navigation icon in the Toolbar
            toolbar.setNavigationIcon(icon);
        }

        // Set the navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // set a title for the CollapsingToolbarLayout
        collapsingToolbarLayout.setTitle("Contacts");


        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.list);


        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        
        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = ContactBookContract.contactEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                /* or
                // Build the URI directly without converting the id to a string
                Uri uri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                */

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, ShowContactBook.this);
            }
        }).attachToRecyclerView(mRecyclerView);


        // Register an AdapterDataObserver to listen for changes in the adapter data
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            // This method is called whenever the whole data set is changed.
            @Override
            public void onChanged() {
                super.onChanged();
                // Whenever the data is changed (items inserted, removed, or modified),
                // this method will be called, and we update the item count in the Toast
                showItemCount();
                // Check if the adapter is empty or not whenever data is changed.
                checkEmpty();
            }

            // This method is called when items are inserted into the adapter.
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                // This method is called when items are inserted into the adapter,
                // so we update the item count in the Toast
                showItemCount();
                // Check if the adapter is empty or not after items are inserted.
                checkEmpty();
            }

            // This method is called when items are removed from the adapter.
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                // This method is called when items are removed from the adapter,
                // so we update the item count in the Toast
                showItemCount();
                // Check if the adapter is empty or not after items are removed.
                checkEmpty();
            }

            // Custom method to check if the adapter is empty and update the visibility of the LottieAnimationView accordingly.
            void checkEmpty() {
                // If the adapter's item count is 0, set the LottieAnimationView visibility to VISIBLE, otherwise set it to GONE.
                lottieAnimationView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
                Toast.makeText(ShowContactBook.this, "Searching", Toast.LENGTH_SHORT).show();
                // TODO : Perform Search View for Filtering Recycler View
            }
        });


        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

    }

    private void showItemCount() {
        // Get the current item count from the adapter
        int count = mAdapter.getItemCount();
        // Create a message with the item count
        String message = count + " Contacts";
        // Show a short-duration Toast with the item count message
        textViewTotalContacts.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, ShowContactBook.this);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(ContactBookContract.contactEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            ContactBookContract.contactEntry._ID + " DESC");

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    // Handle back button press
    @Override
    public void onBackPressed() {
        // Your back button functionality here
        super.onBackPressed();
    }

}








