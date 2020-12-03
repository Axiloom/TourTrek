package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentReference;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Tour;
import com.tourtrek.data.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ToursOfFriendsAdapter extends RecyclerView.Adapter<ToursOfFriendsAdapter.ToursOfFriendsViewHolder> {

    private static final String TAG = "ToursOfFriendsAdapter";
    private final List<Tour> toursDataSet;
    private final Context context;

    public static class ToursOfFriendsViewHolder extends RecyclerView.ViewHolder {

        public TextView tourName;
        public TextView tourLocation;
        public ImageView tourPicture;

        public ToursOfFriendsViewHolder(View view) {
            super(view);
            this.tourName = view.findViewById(R.id.item_tour_name);
            this.tourLocation = view.findViewById(R.id.item_tour_location);
            this.tourPicture = view.findViewById(R.id.item_tour_cover_iv);

        }
    }



    public ToursOfFriendsAdapter(Context context) {
        this.toursDataSet = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ToursOfFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour, parent, false);
        return new ToursOfFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToursOfFriendsViewHolder holder, int position) {
        ((MainActivity) context).findViewById(R.id.friend_tours_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.friend_tours_rv).setVisibility(View.INVISIBLE);

        holder.tourName.setText(toursDataSet.get(position).getName());
        holder.tourLocation.setText(toursDataSet.get(position).getLocation());
        //load picture for tour
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
                        ((MainActivity) context).findViewById(R.id.friend_tours_loading_container).setVisibility(View.INVISIBLE);
                        ((MainActivity) context).findViewById(R.id.friend_tours_rv).setVisibility(View.VISIBLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.tourPicture.setClipToOutline(true);
                        }
                        return false;
                    }
                })
                .into(holder.tourPicture);

        ((MainActivity) context).findViewById(R.id.friend_tours_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.friend_tours_rv).setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return toursDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param tour item to be added
     */
    public void add(Tour tour) {
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
     * Clean all elements of the recycler
     */
    public void clear() {
        toursDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.friend_tours_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.friend_tours_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.friend_tours_rv).setVisibility(View.VISIBLE);
        }
    }
}

