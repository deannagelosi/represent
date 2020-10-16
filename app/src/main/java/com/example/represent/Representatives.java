package com.example.represent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class Representatives extends AppCompatActivity {

    // Declare widgets
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representatives);

        String officialsString = getIntent().getExtras().getString("officials");
        Log.d("Rep activity: officials", officialsString);


        back = findViewById(R.id.back);
        Log.d("back value", "" + back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Representatives.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}