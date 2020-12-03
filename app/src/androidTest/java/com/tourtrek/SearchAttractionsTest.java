//package com.tourtrek;
//
//import com.tourtrek.data.Attraction;
//import com.tourtrek.data.Tour;
//import com.tourtrek.fragments.TourFragment;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.w3c.dom.Attr;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//import static org.junit.Assert.assertEquals;
//
//public class SearchAttractionsTest {
//    TourFragment tourFragment;
//
//    @Before
//    public void setup() {
//        tourFragment = new TourFragment();
//    }
//
//    @Test
//    public void findAttractionTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 1
//        List<Attraction> result = tourFragment.findAttractions(attractions, "attraction2");
//        assertEquals(result.size(), 1);
//    }
//
//    @Test
//    public void findAttractionNoResultTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 0
//        List<Attraction> result = tourFragment.findAttractions(attractions, "attraction8");
//        assertEquals(result.size(), 0);
//    }
//
//    @Test
//    public void sortAttractionsByNameAscendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Name Ascending");
//        assertEquals(result.get(0).getName(), "attraction0");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.size(), 3);
//    }
//
//    @Test
//    public void sortAttractionsByLocationAscendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Location Ascending");
//        assertEquals(result.get(0).getName(), "attraction0");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.size(), 3);
//    }
//
//    @Test
//    public void sortAttractionsByCostAscendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Cost Ascending");
//        assertEquals(result.get(0).getName(), "attraction0");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.size(), 3);
//    }
//
//    @Test
//    public void sortAttractionsByNameDescendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Name Descending");
//        assertEquals(result.get(0).getName(), "attraction2");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.get(2).getName(), "attraction0");
//        assertEquals(result.size(), 3);
//    }
//
//    @Test
//    public void sortAttractionsByLocationDescendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Location Descending");
//        assertEquals(result.get(0).getName(), "attraction2");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.get(2).getName(), "attraction0");
//        assertEquals(result.size(), 3);
//    }
//
//    @Test
//    public void sortAttractionsByCostDescendingTest(){
//        //Create 3 attractions and set fields
//        Attraction attraction0 = new Attraction();
//        Attraction attraction1 = new Attraction();
//        Attraction attraction2 = new Attraction();
//        attraction0.setName("attraction0");
//        attraction0.setLocation("attraction0_location");
//        attraction0.setCost(0);
//
//        attraction1.setName("attraction1");
//        attraction1.setLocation("attraction1_location");
//        attraction1.setCost(1);
//
//        attraction2.setName("attraction2");
//        attraction2.setLocation("attraction2_location");
//        attraction2.setCost(2);
//
//        //Add attractions to a list
//        List<Attraction> attractions = new ArrayList<>();
//        attractions.add(attraction1);
//        attractions.add(attraction0);
//        attractions.add(attraction2);
//
//        //Expected size 3
//        List<Attraction> result = tourFragment.sortedAttractions(attractions, "Cost Descending");
//        assertEquals(result.get(0).getName(), "attraction2");
//        assertEquals(result.get(1).getName(), "attraction1");
//        assertEquals(result.get(2).getName(), "attraction0");
//        assertEquals(result.size(), 3);
//    }
//}