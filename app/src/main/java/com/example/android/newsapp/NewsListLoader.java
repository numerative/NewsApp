package com.example.android.newsapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

/*
 *This is the code for the Loader that is created when OncreateLoader is implemented.
 */
public class NewsListLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    private Uri mUrl;

    public NewsListLoader(Context context, Uri url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        //The QueryUtils class contains all methods that
        String jsonResponse = QueryUtils.fetchJsonData(mUrl);
        return jsonResponse;
    }
}
