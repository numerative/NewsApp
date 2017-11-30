package com.example.android.newsapp;

import android.graphics.Bitmap;

/**
 * Custom Data Type to store webTitle, trailText, Thumbnail, and Last Modified Date
 */

public class Highlight {
    private String mHeadline;
    private String mTrailText;
    private String mLastModified;
    private String mWebUrl;
    private int mIntValueCorrespondingToActualPosition;

    public Highlight(String headline, String trailText, String lastModified, String webUrl, int
            IntValueCorrespondingToActualPosition) {
        mHeadline = headline;
        mTrailText = trailText;
        mLastModified = lastModified;
        mWebUrl = webUrl;
        mIntValueCorrespondingToActualPosition = IntValueCorrespondingToActualPosition;
    }

    //Get the Headline of the Highlight
    public String getHeadline() {
        return mHeadline;
    }

    //Get the TrailText of the Highlight
    public String getTrailText() {
        return mTrailText;
    }

    //Get the LastModified Date
    public String getlastModified() {
        return mLastModified;
    }

    //Get the webUrl
    public String getWebUrl() {
        return mWebUrl;
    }
}
