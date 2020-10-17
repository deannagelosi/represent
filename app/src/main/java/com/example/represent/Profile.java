package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    // Declare widgets
    ImageButton back;
    TextView bio;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back);
        bio = findViewById(R.id.bio);
        profileImage = findViewById(R.id.profile_image);

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

        String imageUrl = "";
        String repBio = "";

        try {
            JSONArray officials = new JSONArray(officialsString);
            Log.d( "official profile", "" + officials.getJSONObject(index));
            JSONObject official = officials.getJSONObject(index);

            String office = official.getString("office");
            String name = official.getString("name");
            String party = official.getString("party");

            imageUrl = official.optString("photoUrl");
            repBio = "Your " + office + " is " + name + " who is a member of the " + party + " party.";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bio.setText(repBio);

        new DownloadImageTask(profileImage).execute(imageUrl);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        // https://stackoverflow.com/a/10868126
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            Log.d("urldisplay", urldisplay);
            if (urldisplay != null) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Drawable placeholder = getApplicationContext().getDrawable(R.drawable.placeholder);
                bmImage.setImageDrawable(placeholder);
            } else {
                bmImage.setImageBitmap(result);
            }
        }

    }
}