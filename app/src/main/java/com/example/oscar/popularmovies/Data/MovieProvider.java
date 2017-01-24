package com.example.oscar.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.oscar.popularmovies.Movie;

/**
 * Created by Oscar on 2017-01-19.
 */

public class MovieProvider extends ContentProvider{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper dbHelper;
    private static final int MOVIES = 100;
    private static final int SPECIFIC_MOVIE = 101;

    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIES);
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", SPECIFIC_MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate(){
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch(match){
            case MOVIES:{
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case SPECIFIC_MOVIE:{
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        System.out.println("Query");
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                System.out.println("Querying movies...");
                cursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return cursor;
            }
            case SPECIFIC_MOVIE: {
                System.out.println("Querying specific movie...");
                cursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return cursor;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case MOVIES:
                numDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            case SPECIFIC_MOVIE:
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch(sUriMatcher.match(uri)){
            case MOVIES:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }else{
                    throw new android.database.SQLException("Failed to insert row");
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIES:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try{
                    for(ContentValues value : values){
                        if (value == null){
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME,
                                    null, value);
                        }catch(SQLiteConstraintException e) {
                            System.out.println("Failed to insert, value already present");
                        }
                        if (_id != -1){
                            numInserted++;
                        }
                    }
                    if(numInserted > 0){
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0){
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case MOVIES:{
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case SPECIFIC_MOVIE: {
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
