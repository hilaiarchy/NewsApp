package com.example.android.newsapp;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving news data from the url.
 */
public final class NewsHelper {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NewsHelper.class.getSimpleName();
    /**
     * value of the SEPARATOR (between the date and time value)
     */
    private static final String SEPARATOR= "T";

    private NewsHelper() {
    }

    public static ArrayList<NewsItem> fetchNewsItemData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        ArrayList<NewsItem> newsItems = extractNewsItem(jsonResponse);

        // Return the list of {@link Earthquake}s
        return newsItems;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static final URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
    * ask HTTP connection
    **/
    public static final String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the user JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * make info readable
     **/
    private static final String readFromStream(InputStream inputStream) throws IOException {
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
    /**
     * get json info
     **/
    public static ArrayList<NewsItem> extractNewsItem(String newsItemJSON) {
        if (TextUtils.isEmpty(newsItemJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // build up a list of Earthquake objects with the corresponding data.

            JSONObject baseJsonResponse = new JSONObject(newsItemJSON);
            JSONObject newsItemObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsItemArray = newsItemObject.getJSONArray("results");

            for (int i = 0; i< newsItemArray.length(); i++){
                JSONObject currentNewsItem = newsItemArray.getJSONObject(i);
                String section = currentNewsItem.getString("sectionName");
                String title = currentNewsItem.getString("webTitle");
                String tag = currentNewsItem.getString("pillarName");
                String dateInISOFRMT =  currentNewsItem.getString("webPublicationDate");
                String url = currentNewsItem.getString("webUrl");

                JSONArray contributor = currentNewsItem.getJSONArray("tags");
                StringBuilder authorName = new StringBuilder();

                for (int j = 0; j< contributor.length(); j++) {
                    JSONObject currentContributor = contributor.getJSONObject(j);
                    if (j == 0) {
                        authorName.append(currentContributor.getString("webTitle"));
                    }
                    else {
                        authorName.append(", \n");
                        authorName.append(currentContributor.getString("webTitle"));
                    }
                }

                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
                SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a");

                String date;
                String time;

                if (!dateInISOFRMT.contains(SEPARATOR)) {
                    date = dateInISOFRMT;
                    time = Resources.getSystem().getString(R.string.no_time);
                } else {
                    String[] separated = dateInISOFRMT.split(SEPARATOR);
                    // contain "yyyy-MM-dd"
                    date = separated[0];
                    // contain "HH:mm:ssZ"
                    time = separated[1].replace("Z","");
                }

                Date dateToFormat = null;
                Date timeToFormat = null;
                String dateToDisplay;
                String timeToDisplay;

                try {
                    dateToFormat = inputDateFormat.parse(date);
                } catch (ParseException e) {

                    e.printStackTrace();
                }
                dateToDisplay = outputDateFormat.format(dateToFormat);

                try {
                    timeToFormat = inputTimeFormat.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeToDisplay = outputTimeFormat.format(timeToFormat);

                NewsItem newsItem = new NewsItem(section,title,tag,authorName,dateToDisplay,timeToDisplay,url);
                newsItems.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the Newsitem JSON results", e);
        }

        // Return the list of news items
        return newsItems;
    }
}
