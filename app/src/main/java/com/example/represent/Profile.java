package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class Profile extends AppCompatActivity {

    // Declare widgets
    ImageButton back;
    ImageView profileImage;
    TextView fullName;
    TextView bio;
    TextView websiteLink;
    TextView phoneNumber;
    ImageButton twitterFollow;
    ImageButton facebookFollow;
    ImageButton youtubeFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = findViewById(R.id.back);
        fullName = findViewById(R.id.full_name);
        bio = findViewById(R.id.bio);
        profileImage = findViewById(R.id.profile_image);
        websiteLink = findViewById(R.id.website_url);
        phoneNumber = findViewById(R.id.phone_number);
        twitterFollow = findViewById(R.id.twitter_icon);
        facebookFollow = findViewById(R.id.facebook_icon);
        youtubeFollow = findViewById(R.id.youtube_icon);

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
        String name = "";
        String repBio = "";
        String websiteUrl = "";
        String phoneNum = "";
        String twitterId = "";
        String facebookId = "";
        String youtubeId = "";

        try {
            JSONArray officials = new JSONArray(officialsString);
            Log.d( "official profile", "" + officials.getJSONObject(index));
            JSONObject official = officials.getJSONObject(index);

            String office = official.getString("office");
            name = official.getString("name");
            String party = official.getString("party");

            imageUrl = official.optString("photoUrl");
            repBio = "Your " + office + " is " + name + " who is a member of the " + party + ".";
            websiteUrl = official.getJSONArray("urls").getString(0);
            phoneNum = official.getJSONArray("phones").getString(0);

            JSONArray socialChannels = official.getJSONArray("channels");
            for (int i = 0; i < socialChannels.length(); i++) {
                JSONObject socialMedia = socialChannels.getJSONObject(i);
                String type = socialMedia.getString("type");
                String id = socialMedia.getString("id");
                if (type.equals("Facebook")) {
                    facebookId = id;
                } else if (type.equals("Twitter")) {
                    twitterId = id;
                } else if (type.equals("YouTube")) {
                    youtubeId = id;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set the Profile UI values
        new DownloadImageTask(profileImage).execute(imageUrl);
        fullName.setText(name);
        bio.setText(repBio);
        websiteLink.setText(websiteUrl);


        SpannableString underlinedNum = new SpannableString(phoneNum);
        underlinedNum.setSpan(new UnderlineSpan(), 0, phoneNum.length(), 0);
        phoneNumber.setText(underlinedNum);

        final Uri phoneUri = Uri.parse("tel:" + phoneNum);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_DIAL, phoneUri);
                startActivity(call);
            }
        });

        setSocialMedia(facebookFollow, Uri.parse("http://facebook.com/" + facebookId));
        setSocialMedia(twitterFollow, Uri.parse("http://twitter.com/" + twitterId));
        setSocialMedia(youtubeFollow, Uri.parse("http://youtube.com/" + youtubeId));
    }

    private void setSocialMedia(final ImageButton socialButton, final Uri uri) {
        socialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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