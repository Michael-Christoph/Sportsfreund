package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewGame extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private EditText inputGame;
    private EditText inputDate;
    private EditText inputTime;
    Button makeGameButton;
    private TextView locationSet;
    final int REQUEST_CODE = 1;
    private Location gameLocation;
    private String noLocation= "no Location found";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";

    double locLat;
    double locLong;


    private ItemAdapter_neu itemAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        Log.d("bla","currentUser: " +currentUser);

        // back-button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputGame = findViewById(R.id.input_name);
        inputDate = findViewById(R.id.input_date);
        inputTime = findViewById(R.id.input_time);

        setupMapButton();
        setupCreateGameButton();
        //setupTextView();
        itemAdapter = MainActivity.getItemAdapter();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }
    @Override
    public void onStart(){
        super.onStart();

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

    private void makeNewGame(){
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent i = new Intent(this,SignUpActivity.class);
            startActivity(i);
        } else {
            String gameName = inputGame.getText().toString();
            String gameDate = inputDate.getText().toString();
            String gameTime = inputTime.getText().toString();

            Game game = new Game(getApplicationContext(),gameName,gameDate,gameTime,locLat,locLong,currentUser.getUid());
            //Game game = new Game(gameName,gameTime,gameLocation,"testid");

            //addGameToDatabase(game);
            itemAdapter.add(game, this);
            //RealtimeDbAdapter.addGame(game);
        }
    }

    /*
    private void makeNewGame() {
        String gameName = inputGame.getText().toString();
        String gameTime = inputTime.getText().toString();
        double gameLat = locLat;
        double gameLong = locLong;
        Game game = new Game(gameName,gameTime,gameLat,gameLong,uid);
        new RealtimeSync().execute(game);

    }
    */
    /*
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
    */

    /*
    private void setupTextView() {

        locationSet = findViewById( R.id.location_set );
        locationSet.setText("dummy");
    }
    */

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
            if (locLat != 0){
                Log.d("NewGame","LocLat: " + locLat);
                Log.d("NewGame", "LocLong" + locLong);
            }
    }}}


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.acount)  {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(this,AccountActivity.class));
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }

        if(id == R.id.games)  {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return false;
    }



}

