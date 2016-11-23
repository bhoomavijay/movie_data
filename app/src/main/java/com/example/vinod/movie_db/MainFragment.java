package com.example.vinod.movie_db;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class MainFragment extends Fragment {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private CustomAdapter mAdapter;
    List<RowItem> rowItems;
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        rowItems= new ArrayList<RowItem>();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovieDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mAdapter =
                new CustomAdapter(
                        getActivity(), // The current context
                        rowItems);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                RowItem buffer = mAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("pic", buffer.getPic())
                        .putExtra("title",buffer.get_movie_name())
                        .putExtra("date",buffer.get_release_date())
                        .putExtra("rating",buffer.get_rating())
                        .putExtra("overview", buffer.get_overview());

                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovieDB() {
        FetchMovieTask movieTask = new FetchMovieTask();


        movieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieDB();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<RowItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private List<RowItem> getMovieDataFromJson(String MovieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_RESULTS = "results";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_OVERVIEW=  "overview";
            final String MOVIE_TITLE=  "title";
            final String MOVIE_DATE=  "release_date";
            final String MOVIE_RATING="vote_average";




            JSONObject MovieJson = new JSONObject(MovieJsonStr);
            JSONArray MovieArray = MovieJson.getJSONArray(MOVIE_RESULTS);



            List<RowItem> rowItems1;
            rowItems1 = new ArrayList<RowItem>();
            rowItems1.clear();
            for(int i = 0; i < MovieArray.length(); i++) {
                JSONObject MovieJson_buffer=MovieArray.getJSONObject(i);
                RowItem newrow=new RowItem(MovieJson_buffer.getString(MOVIE_TITLE),MovieJson_buffer.getString(MOVIE_POSTER_PATH),MovieJson_buffer.getString(MOVIE_DATE),MovieJson_buffer.getString("id"),MovieJson_buffer.getString(MOVIE_OVERVIEW),MovieJson_buffer.getString(MOVIE_RATING));
                rowItems1.add(newrow);
                Log.d(LOG_TAG, MovieJson_buffer.getString(MOVIE_TITLE));

            }

            return rowItems1;

        }
        @Override
        protected List<RowItem> doInBackground(String... params) {



            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortwith = sharedPrefs.getString(
                    getString(R.string.pref_sort),
                    getString(R.string.pref_sort_rat));
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;


            int numDays = 7;

            try {

                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(sortwith)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG,builtUri.toString());
                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                MovieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<RowItem> result) {
            if (result != null) {
                mAdapter.clear();
                for(RowItem i:result)
                    rowItems.add(i);
                Log.d(LOG_TAG, rowItems.get(0).get_movie_name());
                //  for(String temp:result){

                //}


                // New data is back from the server.  Hooray!
            }
        }
    }
}
