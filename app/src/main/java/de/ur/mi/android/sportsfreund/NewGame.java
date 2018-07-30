package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class NewGame extends AppCompatActivity {
    private EditText inputGame;
    private EditText inputTime;
    private TextView locationSet;
    final int REQUEST_CODE = 1;
    private Location gameLocation;
    private String noLocation= "no Location found";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // back-button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputGame = findViewById(R.id.input_game);
        inputTime = findViewById(R.id.input_time);

        setupMapButton();
        setupCreateGameButton();
        setupTextView();


    }

    private void setupCreateGameButton() {
        Button makeGameButton = findViewById(R.id.button_make_new_game);
        makeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewGame();
            }
        });
    }



    private void makeNewGame() {
        String gameName = inputGame.getText().toString();
        String gameTime = inputTime.getText().toString();

        Game game = new Game(gameName,gameTime);
        new RealtimeSync().execute(game);

    }

    private void setupTextView() {
        locationSet = findViewById( R.id.location_set );
        locationSet.setText(getString(R.string.google_api_key));
    }

    private void setupMapButton(){
        Button mapButton = (Button)findViewById(R.id.button_find_place);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMapActivity();
            }
        });
    }

    private void changeToMapActivity() {

        Intent getLocation = new Intent(this, MapsActivity.class);

        startActivityForResult(getLocation,REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode==REQUEST_CODE){
        if(resultCode== Activity.RESULT_OK){

           Bundle extras = data.getExtras();
            double locLat = data.getDoubleExtra(KEY_LOCATION_LAT, 0.000001);
            double locLong = data.getDoubleExtra(KEY_LOCATION_LONG, 0.00001);

            locationSet.setText( Double.toString(locLat) );
    }}}


    }

