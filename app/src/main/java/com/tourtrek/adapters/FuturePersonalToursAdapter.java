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

public class FuturePersonalToursAdapter extends RecyclerView.Adapter<FuturePersonalToursAdapter.FuturePersonalToursViewHolder> {

    private static final String TAG = "FutureToursAdapter";
    private final List<Tour> futurePersonalToursDataSet;
    private final Context context;

    public static class FuturePersonalToursViewHolder extends RecyclerView.ViewHolder {

        public TextView tourName;
        public TextView location;

        public FuturePersonalToursViewHolder(View view) {
            super(view);
            this.tourName = view.findViewById(R.id.item_personal_tour_name);
            this.location = view.findViewById(R.id.item_personal_tour_location);
        }
    }

    public FuturePersonalToursAdapter(Context context) {
        this.futurePersonalToursDataSet = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public FuturePersonalToursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_tour, parent, false);
        return new FuturePersonalToursViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull FuturePersonalToursAdapter.FuturePersonalToursViewHolder holder, int position) {

        ((MainActivity) context).findViewById(R.id.personal_future_tours_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_future_tours_rv).setVisibility(View.INVISIBLE);

        holder.tourName.setText(futurePersonalToursDataSet.get(position).getName());
        holder.tourName.setTextColor(Color.parseColor("#4E1533"));
        holder.location.setText(futurePersonalToursDataSet.get(position).getLocation());
        holder.location.setTextColor(Color.parseColor("#4E1533"));
        holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        ((MainActivity) context).findViewById(R.id.personal_future_tours_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.personal_future_tours_rv).setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return futurePersonalToursDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param tour item to be added
     */
    public void addNewData(Tour tour) {
        for (Tour aTour : futurePersonalToursDataSet) {
            if (aTour.getTourUID().equals(tour.getTourUID()))
                return;
        }
        futurePersonalToursDataSet.add(tour);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param tours list of items to add
     */
    public void addAll(List<Tour> tours) {
        this.futurePersonalToursDataSet.addAll(tours);
        notifyDataSetChanged();
    }

    /**
     * Returns an item from the recycler view
     *
     * @param position index of item to get
     *
     * @return item at the position specified
     */
    public Tour getTour(int position) {
        return futurePersonalToursDataSet.get(position);
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        futurePersonalToursDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.personal_future_tours_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.personal_future_tours_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.personal_future_tours_rv).setVisibility(View.VISIBLE);
        }
    }
}
