package com.example.represent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declare widgets
    ImageButton getGPS;
    Button search;
    Button random;
    EditText searchAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getGPS = findViewById(R.id.getGPS);
        search = findViewById(R.id.search);
        random = findViewById(R.id.random);
        searchAddress = findViewById(R.id.searchAddress);

        // Log.d("getGPS", "" + getGPS);

        // Start Listening for GPS coordinates
        /// 1. Check the app has been granted the right permissions by the user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Log.d("permission-check", "Invalid permissions to perform GPS check");
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
        }
        /// 2. Create Listener for new GPS position
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                double latitude=0;
                double longitude=0;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                // Log.d("latlon", "GPS Changed: " + latitude + ", " + longitude);
            }
        };

        /// 3. Start the Listener
        final LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1, mLocationListener);

        // Add a click event for the button (execute the convert method when clicked)
        getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation(mLocationManager);
            }
        });

        // Add a click event for the button (execute the convert method when clicked)
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentAddress = searchAddress.getText().toString();
                if (currentAddress.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter an Address", Toast.LENGTH_SHORT).show();
                } else {
                    fetchCivicInfo(currentAddress);
                }
            }
        });

        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCivicInfo("random");
            }
        });
    }

    private void fetchLocation(LocationManager mLocationManager) {
        // Reverse Geocoding API Request
        RequestQueue queue = Volley.newRequestQueue(this);
        String API_KEY = ""; // Put API key here
        String type = "result_type=street_address";
        String latLng = currentGPS(mLocationManager);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" + type + "&latlng=" + latLng + "&key=" + API_KEY;

        // Request a json response from the API endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Declare variables
                        JSONObject result;
                        String formattedAddress = "";

                        try {
                            result = (JSONObject)response.getJSONArray("results").get(0);
                            formattedAddress = result.getString("formatted_address");
                        }
                        catch (JSONException e) {
                            // Log.d("errorParse", "Error Parsing JSON Response");
                            e.printStackTrace();
                        }

                        // Log.d("successParse", "Formatted Address: " + formattedAddress);
                        searchAddress.setText("" + formattedAddress);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("error", "Error: " + error);
                        searchAddress.setText("Error Converting Address");
                    }
                });

        // Add the API request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private String currentGPS(LocationManager mLocationManager) {
        String latLng = "0,0";

        // Check Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Log.d("permission-check", "Invalid permissions to perform GPS check");
            // Request Permission
             ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
        } else {
            // Get current gps cords
            Location currentGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latLng = currentGPS.getLatitude() + "," + currentGPS.getLongitude();
            // Log.d("location", "Current GPS: " + latLng);
        }

        return latLng;
    }

    private void fetchCivicInfo(final String currentAddress) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String address = "";
        if (currentAddress.equals("random")) {
            // Pick a random location
            address = randomZipcode();
        } else {
            address = currentAddress;
        }

        String offices = "&includeOffices=true&";
        String levels = "levels=country&";
        String roles = "roles=legislatorLowerBody&roles=legislatorUpperBody&";
        String API_KEY = "key=AIzaSyBRmaiRao6Mwxqr5Luxvnpc5wuTewDl7J4";
        String url = "https://www.googleapis.com/civicinfo/v2/representatives?address=" + address + offices + levels + roles + API_KEY;
        // Log.d("search api url: ", url);

        // Request a json response from the API endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        // Declare variables
                        JSONArray offices;
                        JSONObject divisions;
                        JSONArray officials ;

                        try {
                            offices = response.getJSONArray("offices");
                            divisions = response.getJSONObject("divisions");
                            officials = response.getJSONArray("officials");

                            for (int i = 0; i < offices.length(); i++) {
                                // Loop the offices and find their division
                                JSONObject office = offices.getJSONObject(i);
                                String divisionId = office.getString("divisionId");
                                JSONObject divisionInfo = divisions.getJSONObject(divisionId);

//                                // Associate Official with their Office and Division
                                JSONArray officialIndices = office.getJSONArray("officialIndices");
                                for (int j = 0; j < officialIndices.length(); j++) {
                                    Integer index = officialIndices.getInt(j);
                                    JSONObject official = officials.getJSONObject(index);
                                    official.put("division", divisionInfo.getString("name"));
                                    official.put("office", office.getString("name"));
                                }
                            }

                            // Log.d("officials", "" + officials);

                            // Check if there are three reps
                            if (officials.length() != 3 && currentAddress.equals("random")) {
                                // Random address only has 2 reps
                                // Log.d("Error", "Response had " + officials.length() + " officials");
                                fetchCivicInfo("random"); // Or, pull from list of zips
                            } else if (officials.length() != 3) {
                                // User entered address ony has 2 reps
                                Toast.makeText(MainActivity.this, "Enter Full Address", Toast.LENGTH_SHORT).show();
                            } else {
                                // Address has 3 reps
                                Intent intent = new Intent(MainActivity.this, Representatives.class);
                                intent.putExtra("officials", officials.toString());
                                startActivity(intent);
                            }

                        }
                        catch (JSONException e) {
                            // Log.d("errorParse", "Error Parsing JSON Response");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.d("error", "Error" + error);
                        if (currentAddress.equals("random")) {
                            // Bad random zipcode. Try one of the preset zips
                            // Log.d("Error", "Bad random zip");
                            fetchCivicInfo("random"); // Or, pull from list of zips
                        } else {
                            // User entered bad address
                            Toast.makeText(MainActivity.this, "Enter a Valid Address", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Add the API request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private String randomZipcode() {
        int min = 10000;
        int max = 99950;
        int newZip = (int)(Math.random()*(max-min+1)+min);
        // Log.d("new zip", "" + newZip);
        return String.valueOf(newZip);
    }

}