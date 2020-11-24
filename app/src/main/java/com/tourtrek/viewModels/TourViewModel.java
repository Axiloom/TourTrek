package com.tourtrek.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tourtrek.data.Tour;

public class TourViewModel extends ViewModel {

    private MutableLiveData<Tour> selectedTour = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNewTour = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUserOwned = new MutableLiveData<>();
    private MutableLiveData<Boolean> returnedFromAddAttraction = new MutableLiveData<>();
    private MutableLiveData<Boolean> returnedFromNavigation = new MutableLiveData<>();

    public Tour getSelectedTour() {
        return selectedTour.getValue();
    }

    public void setSelectedTour(Tour selectedTour) {
        this.selectedTour.setValue(selectedTour);
    }

    public Boolean isNewTour() {
        if (isNewTour.getValue() == null) {
            isNewTour.setValue(false);
        }
        return isNewTour.getValue();
    }

    public void setIsNewTour(Boolean isNewTour) {
        this.isNewTour.setValue(isNewTour);
    }

    public Boolean isUserOwned() { return isUserOwned.getValue(); }

    public void setIsUserOwned(Boolean isUserOwned) { this.isUserOwned.setValue(isUserOwned); }

    public void setReturnedFromAddAttraction(Boolean returnedFromAddAttraction) {
        this.returnedFromAddAttraction.setValue(returnedFromAddAttraction);
    }

    /**
     * Signifies whether we are returning from adding an attraction to the tour
     *
     * @return true if we are returning from adding an attraction
     */
    public Boolean returnedFromAddAttraction() {
        if (returnedFromAddAttraction.getValue() == null) {
            returnedFromAddAttraction.setValue(false);
        }
        return returnedFromAddAttraction.getValue();
    }

    public boolean returnedFromNavigation() {
        if (returnedFromNavigation.getValue() == null) {
            returnedFromNavigation.setValue(false);
        }
        return returnedFromNavigation.getValue();
    }

    public void setReturnedFromNavigation(boolean returnedFromNavigation) {
        this.returnedFromNavigation.setValue(returnedFromNavigation);
    }
}
