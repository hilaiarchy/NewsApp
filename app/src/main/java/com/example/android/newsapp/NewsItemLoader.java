package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.ArrayList;

public class NewsItemLoader extends AsyncTaskLoader<ArrayList<NewsItem>> {

    // string for the uRL
    private String nUrl;

    public NewsItemLoader(Context context, String url) {
        super(context);
        nUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //This is an action on the background
    @Override
    public ArrayList<NewsItem> loadInBackground() {
        if (nUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of the news items.
        ArrayList<NewsItem> newsItems = NewsHelper.fetchNewsItemData(nUrl);
        return newsItems;
    }
}
