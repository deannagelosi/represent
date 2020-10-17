package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    // Declare widgets
    ImageButton back;
    TextView bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back);
        bio = findViewById(R.id.bio);

        final String officialsString = getIntent().getExtras().getString("officials");
        final Integer index = getIntent().getExtras().getInt("index");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Representatives.class);
                intent.putExtra("officials", officialsString);
                startActivity(intent);
            }
        });

        String repBio = "";

        try {
            JSONArray officials = new JSONArray(officialsString);
            Log.d( "official profile", "" + officials.getJSONObject(index));
            JSONObject official = officials.getJSONObject(index);

            String office = official.getString("office");
            String name = official.getString("name");
            String party = official.getString("party");
            String imageUrl = official.optString("photoUrl");

            repBio = "Your " + office + " is " + name + " who is a member of the " + party + " party.";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bio.setText(repBio);
    }
}