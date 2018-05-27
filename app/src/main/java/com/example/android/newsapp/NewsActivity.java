package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.TextView;

//The main activity of the app
public class NewsActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<NewsItem>>{

    // GUARDIAN URL for earthquake data
    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search";

    //Constant value for the newsItem loader ID.
    private static final int NEWS_ITEM_LOADER_ID = 1;

    // The adapter for the item list
    private NewsAdapter newsAdapter;

    //TextView that is displayed when the list is empty
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        //The list view for the items from the GUARDIAN_REQUEST_URL
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Setting empty view in case no return value (null) from url
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        //Creating new adapter to populated with the item list
        newsAdapter = new NewsAdapter(this, R.color.listBackground, new ArrayList<NewsItem>());

        newsListView.setAdapter(newsAdapter);

        //when clicked the user will have a pop up to the web address of the specific item
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current newsItem that was clicked on
                NewsItem currentNewsItem = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentNewsItem.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if ((networkInfo != null) && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            /* Initialize the loader. Pass in the int ID constant defined above and pass in null for
            the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            because this activity implements the LoaderCallbacks interface).*/
            loaderManager.initLoader(NEWS_ITEM_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<ArrayList<NewsItem>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        /*String minDate = sharedPrefs.getString(
                getString(R.string.settings_min_date_key),
                getString(R.string.settings_min_date_default)
        );

        String maxDate = sharedPrefs.getString(
                getString(R.string.settings_max_date_key),
                getString(R.string.settings_max_date_default)
        );*/

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `q=new%20music`
        //GUARDIAN_REQUEST_URL="http://content.guardianapis.com/search"
        //Building:"?q=new%20music&show-tags=contributor&api-key=60b8fabf-1bf7-4a65-8c71-86c249e8745e"
        uriBuilder.appendQueryParameter("q", "new%20music");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "60b8fabf-1bf7-4a65-8c71-86c249e8745e");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        /*uriBuilder.appendQueryParameter("from-date", minDate);
        uriBuilder.appendQueryParameter("to-date", maxDate);*/

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new NewsItemLoader(this, uriBuilder.toString());
    }
    // when the data has finished uploading we want to hide the progress bar
    @Override
    public void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> newsItems) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news items found."
        emptyStateTextView.setText(R.string.no_news_items);

        // If there is a valid list of newsItems, then we would add them to the
        // adapter's data set. This will trigger the ListView to update.
        if (newsItems != null && !newsItems.isEmpty()) {
            newsAdapter.addAll(newsItems);
        }
    }
    // when we restarting we want to reset the loader and clear the current data
    @Override
    public void onLoaderReset(Loader<ArrayList<NewsItem>> loader) {
        newsAdapter.clear();
    }

    //inflates the menu bar
    @Override
    // This method initialize the contents of the Activity's options main.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //This method is where we can setup the specific action that occurs when any of the items in the Options Menu are selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

