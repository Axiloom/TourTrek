package com.tourtrek.utilities;

import com.tourtrek.data.Tour;

import java.util.Comparator;

public class TourLengthSorter implements Comparator<Tour> {
    @Override
    public int compare(Tour tour1, Tour tour2) {
        if (tour2.getLength() != null && tour1.getLength() != null) {
            if (tour2.getLength() > tour1.getLength())
                return -1;
        }
        return 1;
    }
}
