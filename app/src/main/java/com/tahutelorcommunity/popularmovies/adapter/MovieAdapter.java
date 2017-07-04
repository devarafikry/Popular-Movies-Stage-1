package com.tahutelorcommunity.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.tahutelorcommunity.popularmovies.R;
import com.tahutelorcommunity.popularmovies.activity.DetailActivity;
import com.tahutelorcommunity.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Fikry-PC on 7/2/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{
    private JSONArray movieJsonArray;

    public MovieAdapter(){

    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String poster_path;
        try{
            holder.pb_loading.setVisibility(View.GONE);
            holder.movie_image.setVisibility(View.VISIBLE);
            JSONObject movie = movieJsonArray.getJSONObject(position);
            poster_path = movie.getString("poster_path");
            Picasso.with(holder.context).load(String.valueOf(NetworkUtils.buildImageUrl(poster_path.substring(1)))).into(holder.movie_image);

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if(movieJsonArray == null){
            return 0;
        }
        else{
            return movieJsonArray.length();
        }
    }

    public void setMovieData(JSONArray movieData){
        this.movieJsonArray = movieData;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.movie_list_item,parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View view;
        Context context = null;
        public final ImageView movie_image;
        public final ProgressBar pb_loading;
        MovieAdapterViewHolder(View view){
            super(view);
            this.view = view;
            context = view.getContext();
            view.setOnClickListener(this);
            movie_image = (ImageView) view.findViewById(R.id.movie_image);
            pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent(context, DetailActivity.class);
            JSONObject dataJson;
            try{
                dataJson = movieJsonArray.getJSONObject(clickedPosition);
                intent.putExtra(DetailActivity.DETAIL_ACTIVITY_EXTRA,dataJson.getString("id"));
            } catch (Exception e){
                e.printStackTrace();
            }
            context.startActivity(intent);
        }
    }
}
