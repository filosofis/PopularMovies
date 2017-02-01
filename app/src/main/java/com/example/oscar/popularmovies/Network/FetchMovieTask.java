package com.example.oscar.popularmovies.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.oscar.popularmovies.BuildConfig;
import com.example.oscar.popularmovies.Movie;
import com.example.oscar.popularmovies.PosterAdapter;
import com.example.oscar.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Oscar on 2017-01-30.
 */

public class FetchMovieTask extends AsyncTask<String, Void,  Movie[]> {

    private PosterAdapter posterAdapter;
    private Context context;
    private SharedPreferences prefs;

    public FetchMovieTask(Context context, PosterAdapter posterAdapter, SharedPreferences prefs ) {
        this.posterAdapter = posterAdapter;
        this.context = context;
        this.prefs = prefs;
    }

    private Movie[] getMoviesFromJson(String movieJSONStr)throws JSONException {
        System.out.println("Geting movies from Json");
        final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/w185";
        final String MOVIE_THUMB_URL = "http://image.tmdb.org/t/p/w92";
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String OVERVIEW = "overview";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String MOVIE_ID = "id";
        String movieTitle;
        String urlPart;
        String overView;
        String releaseDate;
        String originalTitle;
        String voteAverage;
        String movieId;
        JSONObject movieJson = new JSONObject(movieJSONStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);
        Movie[] movieResult = new Movie[movieArray.length()];
        System.out.println("Json length  " + movieArray.length());
        try
        {
            for (int i = 0; i <movieArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);

                movieId = movie.getString(MOVIE_ID);
                movieTitle = movie.getString(TITLE);
                urlPart = movie.getString(POSTER_PATH);
                releaseDate = movie.getString(RELEASE_DATE);
                overView = movie.getString(OVERVIEW);
                originalTitle = movie.getString(ORIGINAL_TITLE);
                voteAverage = movie.getString(VOTE_AVERAGE);
                movieResult[i] = new Movie(
                        movieTitle,
                        MOVIE_BASE_URL + urlPart,
                        MOVIE_THUMB_URL + urlPart,
                        overView,
                        releaseDate,
                        originalTitle,
                        voteAverage,
                        movieId);
            }
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return movieResult;
    }

    @Override
    protected Movie[] doInBackground(String... strings) {
        System.out.println("Doing stuff in background...");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            System.out.println("Trying");
            //final String BASE_URL="https://api.themoviedb.org/3/discover/movie?api_key=";
            final String BASE_URL="http://api.themoviedb.org/3/movie/";
            final String ENDPOINT = prefs.getString(
                    context.getString(R.string.endpoint_key),
                    context.getString(R.string.endpoint_default));
            final String API = "?api_key=";
//                final String SORT = "&sort_by=";
//                String OPTION = prefs.getString(
//                        getString(R.string.pref_sort_key),
//                        getString(R.string.pref_sort_default));
            final String API_KEY = BuildConfig.API_KEY;

            //URL url = new URL(BASE_URL + API_KEY + SORT + OPTION);
            URL url = new URL(BASE_URL + ENDPOINT + API + API_KEY);
            System.out.println("Build URL " + url);

            // Create the request to TheMovieDB, and open the connection
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
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

                /*if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }*/
            movieJsonStr = buffer.toString();

            System.out.println("Movie string: " + movieJsonStr);

        } catch (IOException e) {
            Log.e("", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("", "Error closing stream", e);
                }
            }

            try {
                return getMoviesFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e("", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        int count=0;
        if(movies != null){
            posterAdapter.clear();
            for(Movie movie : movies){
                posterAdapter.add(movie);
                count++;
            }
        }
        System.out.println("Post execute added " + count + " movies");
    }
}