package com.tourtrek.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tourtrek.data.User;

public class FriendViewModel extends ViewModel {

    private MutableLiveData<User> selectedFriend = new MutableLiveData<>();

    public User getSelectedFriend() {
        return selectedFriend.getValue();
    }

    public void setSelectedFriend(User selectedFriend) {
        this.selectedFriend.setValue(selectedFriend);
    }

}
