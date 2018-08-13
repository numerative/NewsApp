package com.example.android.newsapp;

import android.content.Context;
import android.net.Uri;

import com.example.android.newsapp.Utils.QueryUtils;

/*
 *This is the code for the Loader that is created when OncreateLoader is implemented.
 */
public class NewsListLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    private Uri mUrl;

    NewsListLoader(Context context, Uri url) {
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
        return QueryUtils.fetchJsonData(mUrl);
    }
}
