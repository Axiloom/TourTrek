package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Tour;

import java.util.ArrayList;
import java.util.List;

public class TourMarketAdapter extends RecyclerView.Adapter<TourMarketAdapter.TourMarketViewHolder> {

    private static final String TAG = "TourMarketAdapter";
    private List<Tour> toursDataSet;
    private final Context context;
    private List<Tour> toursDataSetCopy;
    private List<Tour> toursDataSetFiltered;

    public static class TourMarketViewHolder extends RecyclerView.ViewHolder {

        public TextView tourName;
        public ImageView coverImage;
        public TextView location;
        public RatingBar rating;

        public TourMarketViewHolder(View view) {
            super(view);
            this.tourName = view.findViewById(R.id.item_tour_name);
            this.coverImage = view.findViewById(R.id.item_tour_cover_iv);
            this.location = view.findViewById(R.id.item_tour_location);
            this.rating = view.findViewById(R.id.item_tour_ratingBar);
        }

    }

    public TourMarketAdapter(Context context) {
        this.toursDataSet = new ArrayList<>();
        this.toursDataSetCopy = new ArrayList<>();
        this.context = context;
        this.toursDataSetFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public TourMarketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour, parent, false);
        return new TourMarketViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TourMarketViewHolder holder, int position) {

        ((MainActivity) context).findViewById(R.id.tour_market_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.tour_market_rv).setVisibility(View.INVISIBLE);

        holder.tourName.setText(toursDataSet.get(position).getName());
        holder.location.setText(toursDataSet.get(position).getLocation());
        holder.rating.setRating((float) toursDataSet.get(position).getRating());

        Glide.with(context)
                .load(toursDataSet.get(position).getCoverImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        System.out.println(e.getMessage());
                        Log.w(TAG, "Error: Tour cover image not loaded");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ((MainActivity) context).findViewById(R.id.tour_market_loading_container).setVisibility(View.INVISIBLE);
                        ((MainActivity) context).findViewById(R.id.tour_market_rv).setVisibility(View.VISIBLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.coverImage.setClipToOutline(true);
                        }
                        return false;
                    }
                })
                .into(holder.coverImage);

        ((MainActivity) context).findViewById(R.id.tour_market_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.tour_market_rv).setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return toursDataSet.size();
    }

    /**
     * Adds a new item to the recycler view
     *
     * @param tour item to be added
     */
    public void addNewData(Tour tour) {
        toursDataSet.add(tour);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param tours list of items to add
     */
    public void addAll(List<Tour> tours) {
        this.toursDataSet.addAll(tours);
        notifyDataSetChanged();
    }

    /**
     * Returns an item from the recycler view
     *
     * @param position index of item to get
     *
     * @return item at the position specified
     */
    public Tour getData(int position) {
        return toursDataSet.get(position);
    }

    /**
     * Gets the recycler view data set
     *
     * @return recycler view data set
     */
    public List<Tour> getDataSet() {
        return toursDataSetCopy;
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        toursDataSet.clear();
        toursDataSet = new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Copies items into toursDataSetCopy and toursDataSetFiltered
     *
     * @param tours list of tours to copy into toursDataSetCopy and toursDataSetFiltered
     */
    public void copyTours(List<Tour> tours){
        this.toursDataSetFiltered = new ArrayList<>(tours);
        this.toursDataSetCopy = new ArrayList<>(tours);
    }

    public List<Tour> getDataSetFiltered() {
        return toursDataSetFiltered;
    }

    public void setDataSetFiltered(List<Tour> dataSet){
        this.toursDataSetFiltered = new ArrayList<>(dataSet);;
    }

}