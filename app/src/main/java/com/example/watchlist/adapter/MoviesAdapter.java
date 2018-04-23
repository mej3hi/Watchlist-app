package com.example.watchlist.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.watchlist.R;
import com.example.watchlist.fragment.movie.MovieDetailsFragment;
import com.example.watchlist.themoviedb.Genre;
import com.example.watchlist.themoviedb.Movie;
import com.example.watchlist.utils.ConvertValue;
import com.example.watchlist.utils.ImageHandler;


import java.util.ArrayList;
import java.util.List;

/**
 * Created year 2017.
 * Author:
 *  Eiríkur Kristinn Hlöðversson
 *  Martin Einar Jensen
 *
 * MoviesAdapter is used to create list of movies.
 */

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Movie> movieList;
    private List<Genre> genreList;
    private Context context;
    private FragmentManager fm;

    private boolean isLoadingAdded = false;

    /**
     * Is the constructor for the MoviesAdapter
     * @param context Context is context
     * @param fm Fm is FragmentManager
     */
    public MoviesAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
        this.movieList = new ArrayList<>();
        this.genreList = new ArrayList<>();
    }

    /**
     * It get the movieList
     * @return It return list of movie
     */
    public List<Movie> getMovieList() {
        return movieList;
    }

    /**
     * Is set list of movie to movieList
     * @param movieList MovieList is list of movie
     */
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    /**
     * Add new view that is either movie view or loading footer view
     * @param parent Parent is the viewGroup
     * @param viewType What view type to display
     * @return It return the new view
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    /**
     * It get the view for the movie
     * @param parent Parent is the viewGroup
     * @param inflater Inflater is LayoutInflater
     * @return It return movie view
     */
    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.movies_recycler_view, parent, false);
        viewHolder = new MoviesAdapter.MoviesVH(v1);
        return viewHolder;
    }

    /**
     * It display the movie at the specified position and add
     * onClick listener that open movie details fragment
     * for that movie
     * @param holder Holder is the viewHolder
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Movie movie = movieList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                MoviesAdapter.MoviesVH moviesVH = (MoviesAdapter.MoviesVH) holder;

                moviesVH.name.setText(movie.getTitle());
                moviesVH.rating.setText(String.format("Rating: %s", ConvertValue.toOneDecimal(movie.getVoteAverage())));
                moviesVH.genre.setText(ConvertValue.genreFromId(movie.getGenreIds(),genreList));
                moviesVH.posterImg.setSmallImg(movie.getPosterPath());
                moviesVH.movieDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong("movieId",movie.getId());
                        movieDetailsFragment.setArguments(bundle);
                        fm.beginTransaction().replace(R.id.main_container, movieDetailsFragment, movieDetailsFragment.getTag()).addToBackStack(null).commit();
                    }
                });

                break;
            case LOADING:
                //Do nothing
                break;
        }

    }

    /**
     * It get item count
     * @return Return 0 if null else the size of movieList
     */
    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    /**
     * It get the item view type in that position
     * @param position The position is from 0 - movieList size.
     * @return It return LOADING if true else ITEM
     */
    @Override
    public int getItemViewType(int position) {
        return (position == movieList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    /**
     * It add movie to movieList
     * @param movie Movie is the movie object
     */
    public void add(Movie movie) {
        movieList.add(movie);
        notifyItemInserted(movieList.size() - 1);
    }

    /**
     * It add list of movie to movieList
     * @param list List is list of movie.
     */
    public void addAll(List<Movie> list){
        for (Movie movie : list) {
            add(movie);
        }
    }

    /**
     * Check whether the movieList is empty
     * @return Return true if it is empty else false
     */
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * It add the loading footer
     */
    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Movie());
    }

    /**
     * It remove the loading footer
     */
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieList.size() - 1;
        Movie item = getItem(position);

        if (item != null) {
            movieList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * It get the movie in that position
     * @param position The position is from 0 - movieList size.
     * @return It return a movie object.
     */
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    /**
     * It add list of genre to genreList
     * @param list Is list of genre
     */
    public void addAllGenre(List<Genre> list) {
        genreList.addAll(list);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * MoviesVH content ViewHolder
     */
    private class MoviesVH extends RecyclerView.ViewHolder {
        private TextView rating;
        private TextView name;
        private TextView genre;
        private ImageHandler posterImg;
        private RelativeLayout movieDetail;

        private MoviesVH(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name_movie_textView);
            rating = (TextView) itemView.findViewById(R.id.rating_movie_textView);
            posterImg = new ImageHandler(context,(ImageView) itemView.findViewById(R.id.poster_movie_imageView));
            genre = (TextView) itemView.findViewById(R.id.genre_movie_textView);
            movieDetail =(RelativeLayout) itemView.findViewById(R.id.movie_recycler);
        }
    }

    /**
     * Is the loading footer ViewHolder
     */
    private class LoadingVH extends RecyclerView.ViewHolder {
        private LoadingVH(View itemView) {
            super(itemView);
        }
    }
}

