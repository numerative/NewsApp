package com.example.android.newsapp;

/**
 * Custom Data Type to store webTitle, trailText, Thumbnail, and Last Modified Date
 */

public class Highlight {
    private String headline;
    private String trailText;
    private String lastModified;
    private String webUrl;
    private String sectionName;
    private String contributorName;
    private int intValueCorrespondingToActualPosition;

    Highlight(String headline, String trailText, String lastModified, String webUrl,
              String sectionName, String contributorName, int intValueCorrespondingToActualPosition) {
        this.headline = headline;
        this.trailText = trailText;
        this.lastModified = lastModified;
        this.webUrl = webUrl;
        this.sectionName = sectionName;
        this.contributorName = contributorName;
        this.intValueCorrespondingToActualPosition = intValueCorrespondingToActualPosition;
    }

    //Get the Headline of the Highlight
    public String getHeadline() {
        return headline;
    }

    //Get the TrailText of the Highlight
    public String getTrailText() {
        return trailText;
    }

    //Get the LastModified Date
    public String getlastModified() {
        return lastModified;
    }

    //Get the webUrl
    public String getWebUrl() {
        return webUrl;
    }

    //Get the sectionName
    public String getSectionName() {
        return sectionName;
    }

    //Get the Author's Name
    public String getContributorName() {
        return contributorName;
    }
}
