package com.tahutelorcommunity.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tahutelorcommunity.popularmovies.R;
import com.tahutelorcommunity.popularmovies.adapter.TrailerAdapter;
import com.tahutelorcommunity.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    public final static String DETAIL_ACTIVITY_EXTRA = "extra_message";
    private Button btn_fav;
    private ScrollView detail_view;
    private ProgressBar pb_loading;
    private RecyclerView trailer_recyclerview;
    private TrailerAdapter trailerAdapter;
    private String index;
    private String api_key ;
    private Intent intent;
    private Toast mToast;
    private String title, release_date, duration, description, vote, poster_path;

    private TextView movie_title, movie_year, movie_duration, movie_description, movie_vote, movie_release_date;
    private ImageView movie_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();

        if(intent.hasExtra(DETAIL_ACTIVITY_EXTRA)){
            index = intent.getStringExtra(DETAIL_ACTIVITY_EXTRA);
        }

        api_key = getString(R.string.tmdb_api_key);
        btn_fav = (Button) findViewById(R.id.btn_fav);
        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mToast!= null){
                    mToast.cancel();
                }
                mToast = Toast.makeText(DetailActivity.this, getResources().getString(R.string.you_favourited_this), Toast.LENGTH_SHORT);
                mToast.show();

            }
        });
        detail_view = (ScrollView) findViewById(R.id.detail_view);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        trailer_recyclerview = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        trailer_recyclerview.setLayoutManager(customLayoutManager);
        trailer_recyclerview.setHasFixedSize(true);
        trailerAdapter = new TrailerAdapter();
        trailer_recyclerview.setAdapter(trailerAdapter);


        movie_title = (TextView) findViewById(R.id.movie_title);
        movie_year = (TextView) findViewById(R.id.movie_year);
        movie_duration = (TextView) findViewById(R.id.movie_duration);
        movie_vote = (TextView) findViewById(R.id.movie_vote);
        movie_description = (TextView) findViewById(R.id.movie_description);
        movie_release_date = (TextView) findViewById(R.id.movie_release_date);

        movie_image = (ImageView) findViewById(R.id.movie_image);

        getData(index);
        getTrailer(index);


    }

    public class CustomLinearLayoutManager extends LinearLayoutManager {
        public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);

        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }

    private void getTrailer(String movieId){
        URL movieDataUrl = NetworkUtils.buildGetVideo(movieId, api_key);
        new MovieTrailerTask().execute(movieDataUrl);
    }
    private void getData(String movieId){
        URL movieDataUrl = NetworkUtils.buildGetData(movieId, api_key);
        new MovieDataTask().execute(movieDataUrl);
    }

    public class MovieDataTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL sortUrl = urls[0];
            String movieData = null;
            try {
                movieData = NetworkUtils.getResponseFromHttpUrl(sortUrl);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return movieData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                detail_view.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                JSONObject data = new JSONObject(s);
                title = data.getString("original_title");
                release_date = data.getString("release_date");
                duration = data.getString("runtime");
                description = data.getString("overview");
                vote = data.getString("vote_average");
                poster_path = data.getString("poster_path").substring(1);

                int dur = Integer.valueOf(duration);
                int min = dur%60;
                int hour = dur/60;

                movie_title.setText(title);
                movie_release_date.setText(release_date);
                movie_year.setText(release_date.substring(0,4));
                movie_duration.setText(hour+"h "+min+"min");
                movie_description.setText(description);
                movie_vote.setText(vote);
                Picasso.with(DetailActivity.this).load(String.valueOf(NetworkUtils.buildImageUrl(poster_path))).into(movie_image);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class MovieTrailerTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL sortUrl = urls[0];
            String movieData = null;
            try {
                movieData = NetworkUtils.getResponseFromHttpUrl(sortUrl);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return movieData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject initialJson = new JSONObject(s);
                JSONArray result = initialJson.getJSONArray("results");
                trailerAdapter.setTrailerData(result);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
