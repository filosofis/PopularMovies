package com.example.oscar.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Created by Oscar on 2016-09-20.
 */
public class posterAdapter extends BaseAdapter {
    Context mContext;
    List<Movie> movies;
    public ImageView poster;
    private RequestListener<String, GlideDrawable> requestListener =
            new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model,
                                   Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            // todo log exception
            System.out.println("Shit went wrong");
            // important to return false so the error placeholder can be placed
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource,
                                       String model,
                                       Target<GlideDrawable> target,
                                       boolean isFromMemoryCache,
                                       boolean isFirstResource) {
            return false;
        }
    };

    public Object getItem(int position){
        return movies.get(position);
    }

    public long getItemId(int position){
        return movies.indexOf(position);
    }
    public int getCount(){
        return movies.size();
    }

    public posterAdapter(Context context, List<Movie> movies){
        this.mContext = context;
        this.movies = movies;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = movies.get(position);
        String imgUrl = "http://api.androidhive.info/images/glide/medium/deadpool.jpg";
        //System.out.println("geting view");
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.movie_item,
                    parent,
                    false);
            poster = (ImageView) convertView.findViewById(R.id.imageView);
        }
        else{
            poster = (ImageView) convertView;
        }
        Glide.with(mContext).load(imgUrl)
                .listener(requestListener)
                .error(R.drawable.testmovie)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(poster);
        System.out.println("Returning convertView");
        //System.out.println(movie.getPosterPath());
        //poster.setImageResource(R.drawable.testmovie);
        return convertView;
    }
}