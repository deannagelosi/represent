package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;


public class Representatives extends AppCompatActivity {

    // Declare widgets
    ImageButton back;
    TextView repDescription1;
    TextView repDescription2;
    TextView repDescription3;
    TextView divisionSenator;
    TextView divisionDistrict;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    Button moreInfo1;
    Button moreInfo2;
    Button moreInfo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representatives);

        back = findViewById(R.id.back);
        repDescription1 = findViewById(R.id.repDescription1);
        repDescription2 = findViewById(R.id.repDescription2);
        repDescription3 = findViewById(R.id.repDescription3);
        divisionSenator = findViewById(R.id.division_senator);
        divisionDistrict = findViewById(R.id.division_district);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        moreInfo1 = findViewById(R.id.moreInfo1);
        moreInfo2 = findViewById(R.id.moreInfo2);
        moreInfo3 = findViewById(R.id.moreInfo3);

        final String officialsString = getIntent().getExtras().getString("officials");
        Log.d("Rep activity: officials", officialsString);

        JSONArray officials;
        List<String> imageURL = new ArrayList<String>();
        List<String> repBio = new ArrayList<String>();
        List<String> divs = new ArrayList<String>();

        try {
            officials = new JSONArray(officialsString);
            for (int i = 0; i < officials.length(); i++) {
                Log.d("each official", "" + officials.getJSONObject(i));
                JSONObject official = officials.getJSONObject(i);

                String office = official.getString("office");
                String name = official.getString("name");
                String party = official.getString("party");
                String division = official.getString("division");

                imageURL.add(official.optString("photoUrl"));
//                repBio.add("Your " + office + " is " + name + " who is a member of the " + party + ".");
                repBio.add(name + " (" + party + ")");
                divs.add(division);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        divisionSenator.setText(divs.get(0));
        divisionDistrict.setText(divs.get(2));

        repDescription1.setText(repBio.get(0));
        repDescription2.setText(repBio.get(1));
        repDescription3.setText(repBio.get(2));

        new DownloadImageTask(image1).execute(imageURL.get(0));
        new DownloadImageTask(image2).execute(imageURL.get(1));
        new DownloadImageTask(image3).execute(imageURL.get(2));


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Representatives.this, MainActivity.class);
                startActivity(intent);
            }
        });

        moreInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Representatives.this, Profile.class);
                intent.putExtra("officials", officialsString);
                intent.putExtra("index", 0);
                startActivity(intent);
            }
        });

        moreInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Representatives.this, Profile.class);
                intent.putExtra("officials", officialsString);
                intent.putExtra("index", 1);
                startActivity(intent);
            }
        });

        moreInfo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Representatives.this, Profile.class);
                intent.putExtra("officials", officialsString);
                intent.putExtra("index", 2);
                startActivity(intent);
            }
        });
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
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
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