package com.tourtrek.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Tour;
import com.tourtrek.data.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsOfFriendsAdapter extends RecyclerView.Adapter<FriendsOfFriendsAdapter.FriendsOfFriendsViewHolder> {

    private static final String TAG = "FriendsOfFriendsAdapter";
    private final List<User> friendsDataSet;
    private final Context context;

    public static class FriendsOfFriendsViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public ImageView friendProfilePicture;

        public FriendsOfFriendsViewHolder(View view) {
            super(view);
            this.friendName = view.findViewById(R.id.item_friend_friendName_tv);
            this.friendProfilePicture = view.findViewById(R.id.item_friend_profile_iv);

        }
    }

    public interface OnFriendListener{
        void onFriendClick(int position);
    }

    public FriendsOfFriendsAdapter(Context context) {
        this.friendsDataSet = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public FriendsOfFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendsOfFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsOfFriendsViewHolder holder, int position) {
        ((MainActivity) context).findViewById(R.id.friend_friends_loading_container).setVisibility(View.VISIBLE);
        ((MainActivity) context).findViewById(R.id.friend_friends_rv).setVisibility(View.INVISIBLE);

        holder.friendName.setText(friendsDataSet.get(position).getUsername());
        holder.friendName.setTextColor(Color.parseColor("#4E1533"));
        holder.friendName.setTextSize(25);

        //load profile picture for friend
        Glide.with((MainActivity) context)
                .load(friendsDataSet.get(position).getProfileImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_profile)
                .into(holder.friendProfilePicture);

        ((MainActivity) context).findViewById(R.id.friend_friends_loading_container).setVisibility(View.INVISIBLE);
        ((MainActivity) context).findViewById(R.id.friend_friends_rv).setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return friendsDataSet.size();
    }

    /**
     * Adds a new item to our recycler view
     *
     * @param friend item to be added
     */
    public void addNewData(User friend) {
        friendsDataSet.add(friend);
        notifyDataSetChanged();
    }

    /**
     * Add a list of items to the recycler view
     *
     * @param friends list of items to add
     */
    public void addAll(List<User> friends) {
        this.friendsDataSet.addAll(friends);
        notifyDataSetChanged();
    }

    /**
     * Add a single user to the recycler view
     *
     * @param user user to be added
     */
    public void add(User user) {
        this.friendsDataSet.add(user);
        notifyDataSetChanged();
    }

    /**
     * Returns an item from the recycler view
     *
     * @param position index of item to get
     *
     * @return item at the position specified
     */
    public User getData(int position) {
        return friendsDataSet.get(position);
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        friendsDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Stop the loading of the progress bar for the recycler view
     */
    public void stopLoading() {
        if (((MainActivity) context).findViewById(R.id.friend_friends_loading_container) != null) {
            ((MainActivity) context).findViewById(R.id.friend_friends_loading_container).setVisibility(View.INVISIBLE);
            ((MainActivity) context).findViewById(R.id.friend_friends_rv).setVisibility(View.VISIBLE);
        }
    }
}
