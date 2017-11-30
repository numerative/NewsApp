package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static android.graphics.BitmapFactory.decodeStream;

/*
    This class accepts a URL at one end and returns JSON response as a String in return
*/

public class QueryUtils {

    public static String fetchJsonData(Uri queryUri) {
        //Calling on a method converting URI into URL
        URL queryURL = getURL(queryUri);
        //Initializing Local Variable
        String jsonResponse = null;
        //Calling on a method to make HTTP request and store its return as the String
        jsonResponse = makeHttpRequest(queryURL);

        return jsonResponse;
    }

    //Method converting URI to URL referenced from fetchJsonData
    private static URL getURL(Uri queryUri) {
        URL queryURL = null;
        try {
            queryURL = new URL(queryUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return queryURL;
    }

    //Method Making a HTTP request and calling other methods but return JSONResponse as String.
    //Referenced from fetchJsonData
    private static String makeHttpRequest(URL queryURL) {
        //Initialising variable
        InputStream inputStream;
        String jsonResponse = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) queryURL.openConnection();
            //Preparing the request to be sent upon opening connection.
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            //Actual attempt to establish connection
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                //Calling requesting inputstream of bits and bytes
                inputStream = urlConnection.getInputStream();
                //Calling a method build String from the input stream and storing the return value as
                //String
                jsonResponse = readFromStream(inputStream);
            }

        } catch (IOException e) {
            //Exception to be caught
            e.printStackTrace();
        }

        return jsonResponse;
    }

    //Converts inputstream to String referenced from makeHttpRequest
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /*
    * For SmallThumbnail Loading
    */
    public static Bitmap fetchThumbnail(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a Bitmap response
        Bitmap smallThumbnail = null;
        try {
            smallThumbnail = makeHttpRequestForBitmap(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return smallThumbnail;
    }

    //Method that makes HTTP request and returns a Bitmap as the response
    private static Bitmap makeHttpRequestForBitmap(URL url) throws IOException {
        Bitmap bitmapResponse = null;

        //If the URL is null, then return early.
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                bitmapResponse = decodeStream(inputStream);

            } else {
                Log.e("Image HTTP error", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return bitmapResponse;
    }

    //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
