package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//The news adapter
public class NewsAdapter extends ArrayAdapter<NewsItem> {

    private int nColorResourceId;

    public NewsAdapter(@NonNull Context context, int ColorResourceId ,@NonNull ArrayList<NewsItem> newsItems) {
        super(context, 0, newsItems);
        nColorResourceId=ColorResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listNewsItemsView = convertView;

        if (listNewsItemsView == null) {
            listNewsItemsView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_item, parent, false);
        }

        NewsItem currentNewsItem = getItem(position);

        TextView sectionTextView = (TextView) listNewsItemsView.findViewById(R.id.section);
        // Get the section from the current news item and
        // set this text on the name TextView
        sectionTextView.setText(currentNewsItem.getSection());

        TextView titleTextView = (TextView) listNewsItemsView.findViewById(R.id.title);
        // Get the Artist Name from the current news item and
        // set this text on the name TextView
        titleTextView.setText(currentNewsItem.getTitle());

        TextView authorTextView = (TextView) listNewsItemsView.findViewById(R.id.author);
        // Get the Artist Name from the current news item and
        // set this text on the name TextView
        authorTextView.setText(currentNewsItem.getAuthor());

        TextView tagTextView = (TextView) listNewsItemsView.findViewById(R.id.tag);
        // Get the tag from the current news item and
        // set this text on the name TextView
        tagTextView.setText(currentNewsItem.getTag());

        TextView dateTextView = (TextView) listNewsItemsView.findViewById(R.id.date);
        // Get the date of the current news item and
        // set this text on the name TextView
        dateTextView.setText(currentNewsItem.getDate());

        TextView timeTextView = (TextView) listNewsItemsView.findViewById(R.id.time);
        // Get the time of the current news item and
        // set this text on the name TextView
        timeTextView.setText(currentNewsItem.getTime());

        // Set the same color for the list item
        View textContainer = listNewsItemsView.findViewById(R.id.item);
        //Find the color that the resource OD maps to
        int color = ContextCompat.getColor(getContext(), nColorResourceId);

        //Set the background color of the text container View
        textContainer.setBackgroundColor(color);
        // Return the whole list item layout
        // so that it can be shown in the ListView

        return listNewsItemsView;
    }
}
