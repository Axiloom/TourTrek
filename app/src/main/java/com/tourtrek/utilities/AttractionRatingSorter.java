package com.tourtrek.utilities;

import com.tourtrek.data.Attraction;

import java.util.Comparator;

public class AttractionRatingSorter implements Comparator<Attraction> {
    @Override
    public int compare(Attraction o1, Attraction o2) {
        if (o1.getRating() > o2.getRating())
            return 1;
        return -1;
    }
}
