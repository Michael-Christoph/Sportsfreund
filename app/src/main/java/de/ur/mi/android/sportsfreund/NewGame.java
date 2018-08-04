package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewGame extends AppCompatActivity {
    private EditText inputGame;
    private EditText inputTime;
    private TextView locationSet;
    final int REQUEST_CODE = 1;
    private Location gameLocation;
    private String noLocation= "no Location found";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";

    private String toastGameAdded = "Spiel wurde erstellt!";
    private String toastAddGameFailed = "Sorry, dein Spiel konnte nicht erstellt werden. Bitte" +
            " versuche es später noch einmal!";

    private ItemAdapter_neu itemAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        mAuth = FirebaseAuth.getInstance();

        // back-button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputGame = findViewById(R.id.input_game);
        inputTime = findViewById(R.id.input_time);

        setupMapButton();
        setupCreateGameButton();
        setupTextView();
        itemAdapter = MainActivity.getItemAdapter();


    }
    @Override
    public void onStart(){
        super.onStart();
        //currentUser = mAuth.getCurrentUser();
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
        String gameLocation = locationSet.getText().toString();


        //Game game = new Game(gameName,gameTime,gameLocation,currentUser.getUid());
        Game game = new Game(gameName,gameTime,gameLocation,"testid");

        //addGameToDatabase(game);
        itemAdapter.add(game);
        //RealtimeDbAdapter.addGame(game);

    }
    private void addGameToDatabase(Game game){
        DatabaseReference mDatabaseGames = FirebaseDatabase.getInstance().getReference("games");
        String gameId = mDatabaseGames.push().getKey();
        mDatabaseGames.child(gameId).setValue(game,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if (databaseError != null){
                    Toast.makeText(getApplicationContext(),toastAddGameFailed,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),toastGameAdded,Toast.LENGTH_SHORT).show();
                }
            }
        });
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

            locationSet.setText( "Lat: " + Double.toString(locLat) + "; Long: " + Double.toString(locLong) );
    }}}


    }

