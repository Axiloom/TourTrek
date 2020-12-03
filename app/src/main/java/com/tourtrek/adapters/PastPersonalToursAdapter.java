package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.Color;
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

public class PastPersonalToursAdapter extends RecyclerView.Adapter<PastPersonalToursAdapter.PastPersonalToursViewHolder> {

    private static final String TAG = "PastPersonalToursAdapter";
    private final List<Tour> pastPersonalToursDataSet;
    private final Context context;

    public static class PastPersonalToursViewHolder extends RecyclerView.ViewHolder {

        public TextView tourName;
        public TextView location;

        public PastPersonalToursViewHolder(View view) {
            super(view);
            this.tourName = view.findViewById(R.id.item_personal_tour_name);
            this.location = view.findViewById(R.id.item_personal_tour_location);
        }

    }

    public PastPersonalToursAdapter(Context context) {
        this.pastPersonalToursDataSet = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public PastPersonalToursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_tour, parent, false);
        return new PastPersonalToursViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull PastPersonalToursViewHolder holder, int position) {

        ((MainActivity) context).findViewById(R.id.personal_past_tours_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_past_tours_rv).setVisibility(View.INVISIBLE);

        holder.tourName.setText(pastPersonalToursDataSet.get(position).getName());
        holder.tourName.setTextColor(Color.parseColor("#4E1533"));
        holder.location.setText(pastPersonalToursDataSet.get(position).getLocation());
        holder.location.setTextColor(Color.parseColor("#4E1533"));
        holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        ((MainActivity) context).findViewById(R.id.personal_past_tours_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_past_tours_rv).setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return pastPersonalToursDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param tour item to be added
     */
    public void addNewData(Tour tour) {
        for (Tour aTour : pastPersonalToursDataSet) {
            if (aTour.getTourUID().equals(tour.getTourUID()))
                return;
        }
        pastPersonalToursDataSet.add(tour);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param tours list of items to add
     */
    public void addAll(List<Tour> tours) {
        this.pastPersonalToursDataSet.addAll(tours);
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
        return pastPersonalToursDataSet.get(position);
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        pastPersonalToursDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.personal_past_tours_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.personal_past_tours_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.personal_past_tours_rv).setVisibility(View.VISIBLE);
        }
    }
}
