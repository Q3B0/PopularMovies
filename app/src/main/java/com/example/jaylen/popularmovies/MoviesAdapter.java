package com.example.jaylen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by jayle on 2016/5/2.
 */
public class MoviesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MovieInfo> movies;
    private LayoutInflater mInflater;
    public MoviesAdapter(Context context,ArrayList<MovieInfo> movies){
        this.context = context;
        this.movies = movies;
    }
    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.movie_pic);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        MovieInfo movieInfo = movies.get(position);
        Picasso.with(context).load(movieInfo.poster_path).into(viewHolder.imageView);
        return convertView;
    }

    class ViewHolder{
        public ImageView imageView;
    }
}
