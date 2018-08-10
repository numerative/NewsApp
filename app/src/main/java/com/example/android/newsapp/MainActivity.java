package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //Declaring the base URL to which the search queries will be appended
    String baseURL = "http://content.guardianapis.com/search?";
    //Initializing other Variables
    Uri queryUri;
    String searchQuery;
    String headline;
    String trailText;
    String publishedDate;
    String thumbnailUrl;
    String webUrl;
    String sectionName;
    SearchView searchBar;
    int i;
    int f;
    // Defined Array values to show in ListView
    ArrayList<Highlight> highlights = new ArrayList<>();
    ArrayList<Bitmap> thumbnails = new ArrayList<>();
    TextView noInternetTextView;
    ProgressBar progressBar;
    private Parcelable listState;
    private RecyclerView rvHighlights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("ListState");
        }
        setContentView(R.layout.activity_main);
        noInternetTextView = findViewById(R.id.no_internet_text_view); //TextView when no internet
        progressBar = findViewById(R.id.progress_bar); //ProgressBar
        //Finding and assigning a floatingActionButton as the Search button to a Variable.
        FloatingActionButton searchButton = findViewById(R.id.search_button);
        //Find the view containing the query
        searchBar = findViewById(R.id.search_bar);
        hideKeyboard(); //Prevent Keyboard from popping up on start up.
        fetchRecentNews(); //Fetch Recent News on start up.
        //Setting an OnClickListener to execute activities on Click.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvHighlights.setVisibility(View.VISIBLE); //If hidden, make it visible
                noInternetTextView.setVisibility(View.GONE); //Always Hidden when clicked
                progressBar.setVisibility(View.VISIBLE); //Displayed the moment button is clicked
                launchSearch();
            }
        });

        if (rvHighlights != null) { //Keep Scroll Position if not null
            rvHighlights.getLayoutManager().onRestoreInstanceState(listState);
        }
        rvHighlights = findViewById(R.id.headlines_recycler); //Recyclerview that displays highlights
    }

    protected void launchSearch() {
        //Clearing previous image data, if any
        thumbnails.clear();
        //Convert the Query to String
        searchQuery = searchBar.getQuery().toString();
        hideKeyboard();
        fetchSearchedNewsQuery();
    }

    protected void fetchSearchedNewsQuery() {
        //Getting a search query specific URL
        queryUri = getUri(baseURL);
        //Get a reference to the LoaderManager, in order to interact with loaders.
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        //Initialize the loader. If loader with the id doesn't exist, it will use the
        //onCreateLoader to create the loader and restart on second instance of search
        //(no reuse as in initLoader)
        loaderManager.restartLoader(1, null, new jsonLoader());
        //Creating 9 placeholder values.
        for (int f = 0; f < 9; f++) {
            thumbnails.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_black_48dp));
        }
    }

    protected void fetchRecentNews() {
        searchQuery = "";
        fetchSearchedNewsQuery();
    }

    protected void hideKeyboard() {
        //Closing the softkeyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        try { //Hiden when search button is clicked
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("Keyboard Not found", e.toString());
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Hidden during onCreate
    }

    /*
     * Method that will create the final query URL
     */
    public Uri getUri(String baseURL) {
        /*
          Building the URL using URI builder
         */
        //Initializing with Uri Builder Variable
        Uri baseUri = Uri.parse(baseURL);
        //Converting the Uri to Uri Builder
        Uri.Builder queryUri = baseUri.buildUpon();

        //Appending the Search Query
        if (!searchQuery.equals("")) { //If searchquery is "", fetch the recent news
            //Excluding the below tag and query from URL fetches the recent news.
            queryUri = queryUri.appendQueryParameter("q", searchQuery);
        }
        //Requesting JSON format
        queryUri = queryUri.appendQueryParameter("format", "json");
        //Appending the API KEY which is set to TEST
        queryUri = queryUri.appendQueryParameter("api-key", BuildConfig.THE_GUARDIAN_API_KEY);

        //Show the following fields for the search page preview
        queryUri = queryUri.appendQueryParameter("show-fields",
                "trailText,headLine,publishedDate,thumbnail");

        //Append query to fetch Contributor's (Author's) name
        queryUri = queryUri.appendQueryParameter("show-tags", "contributor");

        //Return the URI with the given attributes
        return queryUri.build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ListState", rvHighlights.getLayoutManager().onSaveInstanceState());

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
                //for loop to store all items in the array list
                for (i = 0; i < results.length(); i++) {
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
                    JSONObject tag = tagsArray.getJSONObject(0);
                    String contributor = tag.getString("webTitle");
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

                // Create adapter passing in the Json fields
                HighlightsAdapter adapter = new HighlightsAdapter(getApplicationContext(), highlights);
                // Attach the adapter to the recyclerview to populate items
                rvHighlights.setAdapter(adapter);
                // Set layout manager to position the items
                rvHighlights.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            } catch (JSONException e) {
                Log.e("JsonTextError", "Some problem procuring the jsonresponse");
            } catch (NullPointerException e) {
                Log.e("No JSON Response", e.toString());
                noInternetTextView.setVisibility(View.VISIBLE); //Show when JSONResponse is null
                rvHighlights.setVisibility(View.INVISIBLE); //Hide the RecyclerView
                progressBar.setVisibility(View.GONE); //Hide the ProgressBar
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
            //If the images belong to 1 to 9 positions, then the position simply needs to be
            //replaced by another bitmap. If 10th, add.
            if (positionStore < 9) {
                thumbnails.set(positionStore, thumbnail);
            } else {
                thumbnails.add(positionStore, thumbnail);
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

