package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Attraction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CurrentTourAttractionsAdapter extends RecyclerView.Adapter<CurrentTourAttractionsAdapter.CurrentAttractionsViewHolder> {

    private static final String TAG = "CurrentTourAttractionsAdapter";
    private List<Attraction> currentTourAttractionsDataSet;
    private Context context;
    private List<Attraction> currentTourAttractionsDataSetCopy;
    private List<Attraction> currentTourAttractionsDataSetFiltered;

    public static class CurrentAttractionsViewHolder extends RecyclerView.ViewHolder {

        public TextView attractionName;
        public TextView attractionLocation;
        public RatingBar rating;

        public CurrentAttractionsViewHolder (View view) {
            super(view);
            this.attractionName = view.findViewById(R.id.item_attraction_name_tv);
            this.attractionLocation = view.findViewById(R.id.item_attraction_location_tv);
            this.rating = view.findViewById(R.id.item_attraction_ratingBar);
        }

    }

    public CurrentTourAttractionsAdapter (Context context) {
        this.currentTourAttractionsDataSet = new ArrayList<>();
        this.context = context;
        this.currentTourAttractionsDataSetCopy = new ArrayList<>();
        this.currentTourAttractionsDataSetFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public CurrentAttractionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new CurrentAttractionsViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CurrentTourAttractionsAdapter.CurrentAttractionsViewHolder holder, int position) {

        ((MainActivity) context).findViewById(R.id.tour_attractions_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.tour_attractions_rv).setVisibility(View.INVISIBLE);

        holder.attractionName.setText(currentTourAttractionsDataSet.get(position).getName());
        holder.attractionName.setTextColor(Color.parseColor("#FF4859"));
        holder.attractionName.setTextSize(15);
        holder.attractionLocation.setText(currentTourAttractionsDataSet.get(position).getLocation());
        holder.attractionLocation.setTextColor(Color.parseColor("#FF4859"));
        holder.attractionLocation.setTextSize(15);
        holder.rating.setRating((float) currentTourAttractionsDataSet.get(position).getRating());

        // highlighting the attraction item when it is happening
        Attraction attraction = currentTourAttractionsDataSet.get(position);

        if (attraction.getStartDate() != null && attraction.getEndDate() != null) {

            // get instances of the calendar and set the start time for the attraction
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(attraction.getStartDate());

            try {
                String startTime = attraction.getStartTime();
                SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
                Date date = df.parse(startTime);
                calendar.set(Calendar.HOUR, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Timestamp attractionStartDate = new Timestamp(calendar.getTime());
            calendar.setTime(attraction.getEndDate());

            try {
                String endTime = attraction.getEndTime();
                SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
                Date date = df.parse(endTime);
                calendar.set(Calendar.HOUR, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Timestamp attractionEndDate = new Timestamp(calendar.getTime());
            Timestamp now = Timestamp.now();

            // determine if the attraction is happening and change background and text colors
            if (attractionStartDate.compareTo(now) < 0 && attractionEndDate.compareTo(now) > 0) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF4859"));
                holder.attractionName.setTextColor(Color.parseColor("#EEEEEE"));
                holder.attractionLocation.setTextColor(Color.parseColor("#EEEEEE"));
            }
        }

        ((MainActivity) context).findViewById(R.id.tour_attractions_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.tour_attractions_rv).setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return currentTourAttractionsDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param attraction item to be added
     */
    public void addNewData(Attraction attraction) {
        currentTourAttractionsDataSet.add(attraction);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param attractions list of items to add
     */
    public void addAll(List<Attraction> attractions) {
        this.currentTourAttractionsDataSet.addAll(attractions);
        notifyDataSetChanged();
    }

    /**
     * Returns an item from the recycler view
     *
     * @param position index of item to get
     *
     * @return item at the position specified
     */
    public Attraction getData(int position) {
        return currentTourAttractionsDataSet.get(position);
    }

    /**
     * Clean all elements of the recycler view
     */
    public void clear() {
        currentTourAttractionsDataSet.clear();
        currentTourAttractionsDataSet = new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Attraction> getDataSetCopy() {
        return currentTourAttractionsDataSetCopy;
    }

    public List<Attraction> getDataSet() {
        return currentTourAttractionsDataSet;
    }

    public void copyAttractions(List<Attraction> attractions){
        this.currentTourAttractionsDataSetFiltered = new ArrayList<>(attractions);
        this.currentTourAttractionsDataSetCopy = new ArrayList<>(attractions);
    }

    public List<Attraction> getDataSetFiltered() {
        return currentTourAttractionsDataSetFiltered;
    }

    public void setDataSetFiltered(List<Attraction> dataSet){
        this.currentTourAttractionsDataSetFiltered = new ArrayList<>(dataSet);
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.tour_attractions_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.tour_attractions_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.tour_attractions_rv).setVisibility(View.VISIBLE);
        }
    }
}