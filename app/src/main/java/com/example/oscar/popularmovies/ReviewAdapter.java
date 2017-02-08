package com.example.oscar.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter for a Review List into ListView
 */

public class ReviewAdapter extends BaseAdapter {
    Context mContext;
    List<Review> reviews;

    /**
     * ViewHolder pattern for resource optimization
     */
    public static class ViewHolder{
        public final TextView textAuthor;
        public final TextView textContent;

        public ViewHolder(View view){
            textAuthor = (TextView) view.findViewById(R.id.author);
            textContent = (TextView) view.findViewById(R.id.content);
        }
    }

    public ReviewAdapter(Context mContext, List<Review> reviews) {
        this.mContext = mContext;
        this.reviews = reviews;
    }

    public void add(Review review){
        this.reviews.add(review);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
       return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reviews.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.review_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Review currentReview = (Review) getItem(position);
        viewHolder.textAuthor.setText(currentReview.getAuthor());
        viewHolder.textContent.setText(currentReview.getContent());

        return convertView;
    }
    public void clear(){this.reviews.clear();}
}
