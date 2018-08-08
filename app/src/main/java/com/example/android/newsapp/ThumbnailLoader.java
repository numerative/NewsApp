package com.example.android.newsapp;

//Experimental Loader for Thumbnails

import android.content.Context;
import android.graphics.Bitmap;

public class ThumbnailLoader extends android.support.v4.content.AsyncTaskLoader<Bitmap> {
    private String mBitmapUrl;

    ThumbnailLoader(Context context, String url) {
        super(context);
        mBitmapUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Bitmap loadInBackground() {
        //The QueryUtils class contains all methods that
        return QueryUtils.fetchThumbnail(mBitmapUrl);
    }
}
