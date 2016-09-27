package com.example.oscar.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("DetailsFragment created...");

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        System.out.println(intent.getSerializableExtra("movie").toString());

        Movie movie = (Movie)intent.getSerializableExtra("movie");
        TextView title = (TextView) rootView.findViewById(R.id.textViewTitle);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.textViewReleaseDate);
        TextView voteAverage = (TextView) rootView.findViewById(R.id.textViewVoteAverage);
        TextView overView = (TextView) rootView.findViewById(R.id.textViewOverView);
        ImageView imageThumb = (ImageView) rootView.findViewById(R.id.imageViewThumb);

        title.setText(movie.getOriginalTitle());
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(movie.getVoteAverage());
        overView.setText(movie.getOverView());

        Glide.with(getActivity()).load(movie.getThumbPath())
                .error(R.drawable.thumb)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageThumb);
        return rootView;
    }
}
