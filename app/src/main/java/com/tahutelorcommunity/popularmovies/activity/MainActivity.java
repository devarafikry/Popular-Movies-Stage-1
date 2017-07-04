package com.tahutelorcommunity.popularmovies.activity;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tahutelorcommunity.popularmovies.R;
import com.tahutelorcommunity.popularmovies.adapter.MovieAdapter;
import com.tahutelorcommunity.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    private RecyclerView movieRecyclerView;
    private MovieAdapter movieAdapter;
    private String api_key ;
    private ActionBar actionBar;
    private String action_bar_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api_key = getResources().getString(R.string.tmdb_api_key);
        actionBar = getSupportActionBar();
        try{
            action_bar_name = getString(R.string.popular_movies);
            actionBar.setTitle(action_bar_name);
        } catch (Exception e){

        }

        movieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        LinearLayoutManager layoutManager = new GridLayoutManager(this,2);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter();
        movieRecyclerView.setAdapter(movieAdapter);

        String sort = getString(R.string.sort_popular);
        sortMovie(sort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sort;
        switch (item.getItemId()){
            case R.id.action_popular:
                sort = getString(R.string.sort_popular);
                action_bar_name = getString(R.string.popular_movies);
                actionBar.setTitle(action_bar_name);
                sortMovie(sort);
                break;
            case R.id.action_now_playing:
                action_bar_name = getString(R.string.now_playing_movies);
                actionBar.setTitle(action_bar_name);
                sort = getString(R.string.sort_now_playing);
                sortMovie(sort);
                break;
            case R.id.action_top_rated:
                action_bar_name = getString(R.string.top_rated_movies);
                actionBar.setTitle(action_bar_name);
                sort = getString(R.string.sort_top_rated);
                sortMovie(sort);
                break;
            case R.id.action_upcoming:
                action_bar_name = getString(R.string.upcoming_movies);
                actionBar.setTitle(action_bar_name);
                sort = getString(R.string.sort_upcoming);
                sortMovie(sort);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortMovie(String sortBy){
        URL sortMovieUrl = NetworkUtils.buildSortUrl(sortBy, api_key);
        new MovieSortTask().execute(sortMovieUrl);
    }

    public class MovieSortTask extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL sortUrl = urls[0];
            String movieSearchResult = null;
            try {
                movieSearchResult = NetworkUtils.getResponseFromHttpUrl(sortUrl);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return movieSearchResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject initialJson = new JSONObject(s);
                JSONArray  jsonArray = initialJson.getJSONArray("results");
                movieAdapter.setMovieData(jsonArray);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
