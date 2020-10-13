package com.example.represent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //// Get GPS Cords
        // 1. Check the app has been granted the right permissions by the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission-check", "Invalid permissions to perform GPS check");
            // To Do: Currently need to manually set the location permissions in the emulator settings
            // (cont.) Request permission instead
            // example:
            // ActivityCompat.requestPermissions(this, new String[] {
            //                Manifest.permission.ACCESS_FINE_LOCATION,
            //                Manifest.permission.ACCESS_COARSE_LOCATION },
            //        TAG_CODE_PERMISSION_LOCATION);
            return;
        }
        // 2. Create Listener for new GPS position
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                double latitude=0;
                double longitude=0;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("latlon", "LatLon: " + latitude + ", " + longitude);
            }
        };

        // 3. Start the Listener
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, mLocationListener);

        // 4. Example for getting the last known location
        Location gpsTest = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("location", "GPS Test: " + gpsTest.getLatitude() + ", " + gpsTest.getLongitude());

        //// Perform API request with RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_KEY = "AIzaSyBRmaiRao6Mwxqr5Luxvnpc5wuTewDl7J4";
        String address = "94110";
        String url ="https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + API_KEY;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.

                        // String results = response.optString("results");
                        JSONArray arrayResults = new JSONArray();
                        Object firstResult = new Object();

                        try {
                            arrayResults = response.getJSONArray("results");
                            firstResult = arrayResults.get(0);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("success", "Full response: " + response);
                        Log.d("test", "Test: " + firstResult);
                        // textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Log.d("error", "Error: " + error);
                // textView.setText("That didn't work!");
            }
        });

        // Add the API request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}