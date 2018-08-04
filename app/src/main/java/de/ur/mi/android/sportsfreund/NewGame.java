package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewGame extends AppCompatActivity {
    private EditText inputGame;
    private EditText inputTime;
    Button makeGameButton;
    final int REQUEST_CODE = 1;
    private Location gameLocation;
    private String noLocation= "no Location found";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";

    double locLat;
    double locLong;

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


    }

    private void setupCreateGameButton() {
            makeGameButton = findViewById(R.id.button_make_new_game);
            makeGameButton.setEnabled( false );
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
        double gameLat = locLat;
        double gameLong = locLong;

        Game game = new Game(gameName,gameTime, gameLat, gameLong);
        new RealtimeSync().execute(game);

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
            locLat = data.getDoubleExtra(KEY_LOCATION_LAT, 0.000001);
            locLong = data.getDoubleExtra(KEY_LOCATION_LONG, 0.00001);

            makeGameButton.setEnabled( true );
    }}}


    }

