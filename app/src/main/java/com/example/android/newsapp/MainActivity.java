package com.example.android.newsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.android.newsapp.Utils.QueryUtils;
import com.example.android.newsapp.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    //Declaring the base URL to which the search queries will be appended
    final String BASE_URL = "http://content.guardianapis.com/search?";
    //Initializing other Variables
    Uri queryUri;
    String searchQuery, headline, trailText, publishedDate, thumbnailUrl, webUrl, sectionName,
            contributor;
    int i, f, resultsLength;
    // Defined Array values to show in ListView
    ArrayList<Highlight> highlights = new ArrayList<>();
    ArrayList<Bitmap> thumbnails = new ArrayList<>();
    @BindView(R.id.search_bar)
    SearchView searchBar;
    @BindView(R.id.empty_state_text_view)
    TextView emptyStateTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.headlines_recycler)
    RecyclerView rvHighlights;
    @BindView(R.id.main_activity_settings_button)
    Button settingsButton;
    private Parcelable listState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("ListState");
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        searchBar.setOnQueryTextListener(this); //Set the listener on searchView
        Utils.hideKeyboard(MainActivity.this); //Prevent Keyboard from popping up on start up.
        onQueryTextSubmit(""); //Fetch Recent News on start up.

        if (progressBar.getVisibility() == View.GONE) { //Keep Scroll Position if ProgressBar GONE
            rvHighlights.getLayoutManager().onRestoreInstanceState(listState);
        }
        settingsButton.setOnClickListener(this); //Setting listener on the button
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelable("ListState", rvHighlights.getLayoutManager().onSaveInstanceState());
        } catch (NullPointerException e) {
            Log.e("Issue with Parcelable", e.toString());
        }
    }

    private void setEmptyState() {
        emptyStateTextView.setVisibility(View.VISIBLE); //Show when JSONResponse is null
        rvHighlights.setVisibility(View.INVISIBLE); //Hide the RecyclerView
    }

    @Override
    public boolean onQueryTextSubmit(String searchString) {
        searchQuery = searchString;
        rvHighlights.setVisibility(View.VISIBLE); //If hidden, make it visible
        emptyStateTextView.setVisibility(View.GONE); //Always Hidden when clicked
        progressBar.setVisibility(View.VISIBLE); //Displayed the moment button is clicked
        //Clearing previous image data, if any
        thumbnails.clear();
        Utils.hideKeyboard(MainActivity.this);
        queryUri = QueryUtils.getUri(BASE_URL, searchQuery); //Getting a search query specific URL

        //Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();
        //Initialize the loader. If loader with the id doesn't exist, it will use the
        //onCreateLoader to create the loader and restart on second instance of search
        //(no reuse as in initLoader)
        loaderManager.restartLoader(1, null, new jsonLoader());

        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.main_activity_settings_button):
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            default:
                break;
        }
    }

    //First LoaderCallBack implementation
    private class jsonLoader implements android.support.v4.app.LoaderManager.LoaderCallbacks<String> {

        @NonNull
        @Override
        public android.support.v4.content.Loader<String> onCreateLoader(int i, Bundle argument) {
            return new NewsListLoader(getApplicationContext(), queryUri);
        }

        //The following code will be executed when the Loader has finished Loading.
        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<String> loader, String jsonResponse
        ) {
            progressBar.setVisibility(View.GONE); //Hide when Loading is finished

            try {
                JSONObject jsonObjectAsResponse = new JSONObject(jsonResponse);
                JSONObject response = jsonObjectAsResponse.getJSONObject("response");
                JSONArray results = response.getJSONArray("results");
                highlights.clear();
                resultsLength = results.length();
                //for loop to store all items in the array list
                for (i = 0; i < resultsLength; i++) {
                    JSONObject webTitleArray = results.getJSONObject(i);
                    headline = webTitleArray.getString("webTitle");
                    JSONObject fields = webTitleArray.getJSONObject("fields");
                    trailText = fields.getString("trailText");
                    publishedDate = webTitleArray.getString("webPublicationDate");
                    //Link for Thumbnail
                    thumbnailUrl = fields.getString("thumbnail");
                    //Link to open the article
                    webUrl = webTitleArray.getString("webUrl");
                    //Section Name to which the article belongs
                    sectionName = webTitleArray.getString("sectionName");

                    //Getting Author's Name
                    JSONArray tagsArray = webTitleArray.getJSONArray("tags");
                    if (!tagsArray.isNull(0)) { //Parse only if contributor array found
                        JSONObject tag = tagsArray.getJSONObject(0);
                        contributor = tag.getString("webTitle");
                    } else { // else just set value to ""
                        contributor = "";
                    }
                    //Create new object
                    Highlight highlight = new Highlight(headline, trailText, publishedDate, webUrl,
                            sectionName, contributor, i);
                    //Add the new highlight to the ArrayList
                    highlights.add(highlight);
                    //Initializing the Loader. Fingers crossed
                    android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                    //Initialize the loader. If loader with the id doesn't exist, it will use the
                    //onCreateLoader to create the loader and restart on second instance of search
                    //(no reuse as in initLoader)
                    loaderManager.restartLoader(i, null, new thumbnailLoader());
                }

                if (highlights.size() != 0) { //If highlights found set adapter
                    // Create adapter passing in the Json fields
                    HighlightsAdapter adapter = new HighlightsAdapter(getApplicationContext(), highlights);
                    // Attach the adapter to the recyclerview to populate items
                    rvHighlights.setAdapter(adapter);
                    // Set layout manager to position the items
                    rvHighlights.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                } else { //If no highlights found, set empty state
                    setEmptyState();
                    emptyStateTextView.setText(R.string.no_data_found_string);
                }
            } catch (JSONException e) {
                Log.e("JsonTextError", e.toString());
            } catch (NullPointerException e) {
                Log.e("No JSON Response", e.toString());
                setEmptyState();
                emptyStateTextView.setText(R.string.no_data_found_string);
            }

            //Creating results minus 1 placeholder values.
            for (int f = 0; f < resultsLength - 1; f++) {
                thumbnails.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_black_48dp));
            }
        }

        //Compulsory code. Specifies what happens when the Loader is reset.
        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {
            //Nothing here. On purpose.
        }
    }

    //Second Image LoaderCallback implementation
    private class thumbnailLoader implements android.support.v4.app.LoaderManager.LoaderCallbacks<Bitmap> {
        int positionStore;

        @NonNull
        @Override
        public android.support.v4.content.Loader<Bitmap> onCreateLoader(int i, Bundle argument) {
            positionStore = i;
            return new ThumbnailLoader(getApplicationContext(), thumbnailUrl);
        }

        //To be executed on finishing Loading
        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<Bitmap> loader, Bitmap thumbnail) {
            //If the images belong to 1 to length minus 1 positions, then the position simply needs to be
            //replaced by another bitmap. If 10th, add.
            try {
                if (positionStore < resultsLength - 1) {
                    thumbnails.set(positionStore, thumbnail);
                } else {
                    thumbnails.add(positionStore, thumbnail);
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e("quick button presses", e.toString());
            }
            //When all 10 places are filled, the following conditions of code are true.
            if (thumbnails.size() == highlights.size()) {
                // Lookup the recyclerview in activity layout
                rvHighlights = findViewById(R.id.headlines_recycler);
                //Create adapter passing in the Bitmap images
                ThumbnailAdapter thumbnailsAdapter = new ThumbnailAdapter(getApplicationContext(),
                        highlights, thumbnails);
                // Attach the adapter to the recyclerview to populate items
                rvHighlights.setAdapter(thumbnailsAdapter);
                // Set layout manager to position the items
                rvHighlights.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        }

        //Compulsory code. Specifies what happens when the Loader is reset.
        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {
            //Nothing here. On purpose.
        }
    }
}
