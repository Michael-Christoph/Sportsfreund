package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewGame extends AppCompatActivity {
    private Button mapButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // back-button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupMapButton();


    }

    private void setupMapButton(){
        mapButton = (Button)findViewById(R.id.button_find_place);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMapActivity();
            }
        });
    }

    private void changeToMapActivity() {

        Intent i = new Intent(this, MapsActivity.class);

        startActivity(i);
    }
    }

