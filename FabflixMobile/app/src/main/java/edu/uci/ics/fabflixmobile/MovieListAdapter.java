package edu.uci.ics.fabflixmobile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qiaohe on 3/3/18.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title, year, director, genres, stars;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            year = (TextView) view.findViewById(R.id.year);
            director = (TextView) view.findViewById(R.id.director);
            genres = (TextView) view.findViewById(R.id.genre);
            stars = (TextView) view.findViewById(R.id.star);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieListAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String movie = mDataset[position];
        try {
            JSONObject jObject = new JSONObject(movie);

            holder.title.setText(jObject.getString("movie_title"));
            holder.year.setText("Year: "+jObject.getString("movie_year"));
            holder.director.setText("Director: "+ jObject.getString("movie_director"));
            holder.genres.setText("Genres: "+ jObject.getString("movie_genres"));
            holder.stars.setText("Stars: "+ jObject.getString("movie_stars"));
        }
        catch (JSONException e) {

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}

