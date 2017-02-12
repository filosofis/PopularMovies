package com.example.oscar.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Oscar on 2017-02-12.
 */

public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Trailer> trailers;

    private static class ViewHolder{
        private final TextView trailerName;

        private ViewHolder(View view){
            trailerName = (TextView) view.findViewById(R.id.trailer_name);
        }
    }

    public TrailerAdapter(Context mContext, List<Trailer> trailers){
        this.mContext = mContext;
        this.trailers = trailers;
    }

    public void add(Trailer trailer){
        this.trailers.add(trailer);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return trailers.indexOf(position);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.trailer_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Trailer currentTrailer = (Trailer) getItem(position);
        viewHolder.trailerName.setText(currentTrailer.getName());

        return convertView;
    }
    public void clear(){
        this.trailers.clear();
    }
}
