package com.example.oscar.popularmovies.Network;

import android.os.AsyncTask;
import android.util.Log;
import com.example.oscar.popularmovies.BuildConfig;
import com.example.oscar.popularmovies.Review;
import com.example.oscar.popularmovies.ReviewAdapter;
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
 * Fetches reviews for a given movie from TheMovieDB
 */

public class FetchReviewsTask extends AsyncTask<String, Void, Review[]>{

    private ReviewAdapter reviewAdapter;

    public FetchReviewsTask(ReviewAdapter reviewAdapter){
        this.reviewAdapter = reviewAdapter;
    }

    private Review[] getReviewsFromJson(String reviewJSONstr)throws JSONException{

        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        String reviewAuthor;
        String reviewContent;

        JSONObject reviewJson = new JSONObject(reviewJSONstr);
        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);
        Review[] reviewResult = new Review[reviewArray.length()];

        try{
            for(int i=0; i<reviewArray.length(); i++){
                JSONObject review = reviewArray.getJSONObject(i);
                reviewAuthor = review.getString(AUTHOR);
                reviewContent = review.getString(CONTENT);

                reviewResult[i] = new Review(reviewAuthor, reviewContent);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return reviewResult;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonStr = null;

        try{
            final String BASE_URL="http://api.themoviedb.org/3/movie/";
            final String API = "/reviews?api_key=";
            final String API_KEY = BuildConfig.API_KEY;
            final String MOVIE_ID = params[0];

            URL url = new URL(BASE_URL + MOVIE_ID + API + API_KEY);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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

            reviewJsonStr = buffer.toString();
        }
        catch(IOException e){
            Log.e("", "Error ", e);
            return null;
        }finally{
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
                return getReviewsFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e("", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return new Review[0];
    }

    @Override
    protected void onPostExecute(Review[] reviews) {
        int count=0;
        if(reviews != null){
            reviewAdapter.clear();
            for(Review review : reviews){
                reviewAdapter.add(review);
                count++;
            }
        }
        System.out.println("Post execute added " + count + " reviews");
    }
}
