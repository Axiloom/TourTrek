package com.tourtrek.utilities;

import com.tourtrek.data.Tour;

import java.util.Comparator;

public class TourRatingSorter implements Comparator<Tour> {
    @Override
    public int compare(Tour o1, Tour o2) {
        if (o1.getRating() > o2.getRating())
            return 1;
        return -1;
    }

}
