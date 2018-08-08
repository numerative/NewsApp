package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class HighlightsAdapter extends RecyclerView.Adapter<HighlightsAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private ArrayList<Highlight> mHighlights;
    //Store the context for easy access
    private Context mContext;

    //Pass in the string array into the constructor
    HighlightsAdapter(Context context, ArrayList<Highlight> highlights) {
        mHighlights = highlights;
        mContext = context;
    }

    //Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public HighlightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate the custom layout
        View highlightView = inflater.inflate(R.layout.list_item, parent, false);

        //Return a new holder instance
        return new ViewHolder(highlightView);
    }

    //Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull HighlightsAdapter.ViewHolder viewHolder, int position) {
        //Get the data model based on position
        Highlight currentHighlight = mHighlights.get(position);

        //Set headline available at the current position
        viewHolder.headlineTextView.setTextColor((Color.rgb(0, 0, 0)));
        viewHolder.headlineTextView.setText(currentHighlight.getHeadline());
        //Set trailtext available at the current position
        viewHolder.trailTextView.setTextColor((Color.rgb(0, 0, 0)));
        viewHolder.trailTextView.setText(currentHighlight.getTrailText());
        //Set date available at the current position
        viewHolder.dateTextView.setTextColor((Color.rgb(0, 0, 0)));
        //Convert to a readable date format
        String jSDateFormat = currentHighlight.getlastModified();

        //Code to Parse the date into desired format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat destFormat = new SimpleDateFormat("MMM d, yyyy hh:mm a");
        Date date = null;
        try {
            date = sourceFormat.parse(jSDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = destFormat.format(date);
        if (!formattedDate.equals("")) { //To be only set if a date value is found
            //Setting the Text to the View
            viewHolder.dateTextView.setText(formattedDate);
        }

        String contributorName = currentHighlight.getContributorName();
        viewHolder.contributorTextView.setTextColor((Color.rgb(0, 0, 0)));
        if (!contributorName.equals("")) { //To be only set if a contributor name is found
            viewHolder.contributorTextView.setText(contributorName);
        }

        //Setting Section Name
        viewHolder.sectionNameTextView.setTextColor((Color.rgb(0, 0, 0)));
        viewHolder.sectionNameTextView.setText(currentHighlight.getSectionName());
    }


    //Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mHighlights.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView headlineTextView;
        private TextView trailTextView;
        private TextView dateTextView;
        private TextView contributorTextView;
        private TextView sectionNameTextView;
        private Context context;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            headlineTextView = itemView.findViewById(R.id.headline);
            trailTextView = itemView.findViewById(R.id.trail_text);
            dateTextView = itemView.findViewById(R.id.published_date);
            sectionNameTextView = itemView.findViewById(R.id.section_name);
            contributorTextView = itemView.findViewById(R.id.contributor_name);
            //Store the context
            context = getContext();
            //Attach a click listener to this entire row view
            itemView.setOnClickListener(this);
        }

        //Handles the row being clicked
        @Override
        public void onClick(View view) {
            int position1 = getAdapterPosition(); //gets item position
            Highlight highlight = mHighlights.get(position1);
            String uri = highlight.getWebUrl();
            Uri parsed = Uri.parse(uri);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, parsed);
            context.startActivity(webIntent);
        }
    }

}