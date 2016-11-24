/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vinod.movie_db;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #Movie_DB1 http://image.tmdb.org/t/p/w342//";
        private String movie_pic,movie_title,movie_overview,movie_date,movie_rating;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        /**
         * Called to do initial creation of a fragment.  This is called after
         * {@link #onAttach(Activity)} and before
         * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
         * <p/>
         * <p>Note that this can be called while the fragment's activity is
         * still in the process of being created.  As such, you can not rely
         * on things like the activity's content view hierarchy being initialized
         * at this point.  If you want to do work once the activity itself is
         * created, see {@link #onActivityCreated(Bundle)}.
         *
         * @param savedInstanceState If the fragment is being re-created from
         *                           a previous saved state, this is the state.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null ) {
                movie_pic = intent.getStringExtra("pic");
                movie_title = intent.getStringExtra("title");
                movie_date = intent.getStringExtra("date");
                movie_overview = intent.getStringExtra("overview");
                movie_rating=intent.getStringExtra("rating");

                ImageView imageView= (ImageView) rootView.findViewById(R.id.detail_image);
                Picasso.with(rootView.getContext()).load("http://image.tmdb.org/t/p/w185//" + movie_pic).into(imageView);
                TextView movie_name= (TextView) rootView.findViewById(R.id.movie_name);
                movie_name.setText(movie_title);
                TextView date= (TextView) rootView.findViewById(R.id.date);
                date.setText("Release Date:"+movie_date);
                TextView overview= (TextView) rootView.findViewById(R.id.overview);
                overview.setText(movie_overview);
                TextView rating= (TextView) rootView.findViewById(R.id.rating);
                rating.setText("Rating: "+movie_rating+"/10");

            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    FORECAST_SHARE_HASHTAG+movie_pic);
            return shareIntent;
        }
    }
}
