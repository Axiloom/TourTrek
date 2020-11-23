package com.tourtrek.utilities;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.viewModels.AttractionViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Weather {

    private static final String TAG = "Weather";
    private static final String API_KEY = "983ba7e1ece3dbb38be99de82bc61fe2";

    public static void getWeather(String city, String state, Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "," + state + "&appid=" + API_KEY;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject daily = response.getJSONObject("daily");
                    long date = daily.getLong("dt");
                    Date date1 = new Date(date);
                    System.out.println("TEST");
                } catch (JSONException e) {
                    Log.e(TAG, "Error pulling data from JSON", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error retrieving weather for " + state + "," + city + " : " + error.getLocalizedMessage());
            }
        });

        // Add the request to the queue
        queue.add(jsonArrayRequest);

    }

    public static void getWeather(double lat, double lon, Context context) {

        HashMap<String, String> weather = new HashMap<>();

        // Initialize attractionMarketViewModel to get the current attraction
        AttractionViewModel attractionViewModel = new ViewModelProvider((MainActivity)context).get(AttractionViewModel.class);

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&units=imperial&exclude=minutely" + "&appid=" + API_KEY;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray daily = response.getJSONArray("daily");

                    JSONObject day = daily.getJSONObject(0);
                    long unixDate = day.getLong("dt") * (long) 1000;
                    JSONObject temp = day.getJSONObject("temp");
                    double dayTempDouble = temp.getDouble("day");
                    String dayTempString = String.valueOf(dayTempDouble);
                    String dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    JSONArray weatherArray = day.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String main = weatherObject.getString("main");
                    Date date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(1);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(2);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(3);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(4);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(5);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(6);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    day = daily.getJSONObject(7);
                    unixDate = day.getLong("dt") * (long) 1000;
                    temp = day.getJSONObject("temp");
                    dayTempDouble = temp.getDouble("day");
                    dayTempString = String.valueOf(dayTempDouble);
                    dayTemp = dayTempString.substring(0, dayTempString.indexOf("."));
                    weatherArray = day.getJSONArray("weather");
                    weatherObject = weatherArray.getJSONObject(0);
                    main = weatherObject.getString("main");
                    date = new Date(unixDate);
                    weather.put(date.toString(), main + " " + dayTemp);

                    // Only update the view model if we have an attraction selected
                    if (attractionViewModel.getSelectedAttraction() != null) {
                        attractionViewModel.getSelectedAttraction().setWeather(weather);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error pulling data from JSON", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error retrieving weather for " + lat + "," + lon + " : " + error.getLocalizedMessage());
            }
        });

        // Add the request to the queue
        queue.add(jsonArrayRequest);
    }
}
