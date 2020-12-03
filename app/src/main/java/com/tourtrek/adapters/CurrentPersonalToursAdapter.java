package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Tour;

import java.util.ArrayList;
import java.util.List;

public class CurrentPersonalToursAdapter extends RecyclerView.Adapter<CurrentPersonalToursAdapter.CurrentPersonalToursViewHolder> {

    private static final String TAG = "CurrentPersonalToursAdapter";
    private final List<Tour> currentPersonalToursDataSet;
    private final Context context;

    public static class CurrentPersonalToursViewHolder extends RecyclerView.ViewHolder {

        public TextView tourName;
        public TextView location;

        public CurrentPersonalToursViewHolder(View view) {
            super(view);
            this.tourName = view.findViewById(R.id.item_personal_tour_name);
            this.location = view.findViewById(R.id.item_personal_tour_location);
        }

    }

    public CurrentPersonalToursAdapter(Context context) {
        this.currentPersonalToursDataSet = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public CurrentPersonalToursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_tour, parent, false);
        return new CurrentPersonalToursViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CurrentPersonalToursAdapter.CurrentPersonalToursViewHolder holder, int position) {

        ((MainActivity) context).findViewById(R.id.personal_current_tours_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_current_tours_rv).setVisibility(View.INVISIBLE);

        holder.tourName.setText(currentPersonalToursDataSet.get(position).getName());
        holder.tourName.setTextColor(Color.parseColor("#4E1533"));
        holder.location.setText(currentPersonalToursDataSet.get(position).getLocation());
        holder.location.setTextColor(Color.parseColor("#4E1533"));
        holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        ((MainActivity) context).findViewById(R.id.personal_current_tours_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_current_tours_rv).setVisibility(View.VISIBLE);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return currentPersonalToursDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param tour item to be added
     */
    public void addNewData(Tour tour) {
        for (Tour aTour : currentPersonalToursDataSet) {
            if (aTour.getTourUID().equals(tour.getTourUID()))
                return;
        }
        currentPersonalToursDataSet.add(tour);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param tours list of items to add
     */
    public void addAll(List<Tour> tours) {
        this.currentPersonalToursDataSet.addAll(tours);
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
        return currentPersonalToursDataSet.get(position);
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        currentPersonalToursDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.personal_current_tours_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.personal_current_tours_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.personal_current_tours_rv).setVisibility(View.VISIBLE);
        }
    }
}
