package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "GameDetails";

    private TextView type,time,date,textViewParticipants;
    private String participantsString = "";
    private Button buttonShowLocation;
    private Button buttonParticipate;
    private Button buttonResign;

    private ItemAdapter itemAdapter;
    private Game game;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        mAuth = FirebaseAuth.getInstance();
        itemAdapter = MainActivity.getItemAdapter();

        mDrawerLayout = findViewById(R.id.detailsDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.gameDetails_ActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))  {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setup()  {
        Intent intent = getIntent();
        game = intent.getParcelableExtra("game");
        type = findViewById(R.id.title);
        type.setText(game.getGameName());
        time = findViewById(R.id.time);
        time.setText(game.getGameTime());
        date = findViewById(R.id.date);
        date.setText(game.getGameDate());

        textViewParticipants = findViewById(R.id.participants);
        for (String participantId: game.getParticipants()){
            participantsString += "\u26F9";
        }
        textViewParticipants.setText(participantsString);

        setupParticipateAndResignButtons();

        buttonShowLocation = findViewById(R.id.showOnMaps);
        buttonShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationOnMaps();
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showLocationOnMaps() {
        Intent showGameLocation = new Intent(this, MapsActivity.class);
        double locLong = game.getGameLong();
        double locLat = game.getGameLat();
        showGameLocation.putExtra(GlobalVariables.KEY_LOCATION_LAT_D,locLat);
        showGameLocation.putExtra(GlobalVariables.KEY_LOCATION_LONG_D,locLong );
        showGameLocation.putExtra(GlobalVariables.KEY_GAME_NAME, game.getGameName() );
        startActivity(showGameLocation);
    }

    private void setupParticipateAndResignButtons(){
        buttonParticipate = findViewById(R.id.button_participate);
        buttonParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null){
                    Log.d(LOG_TAG,"buttonParticipate clicked, no user found ");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(mAuth.getCurrentUser().getUid())){
                        Log.d(LOG_TAG,"buttonParticipate-onClick-else-if: should never get to this place");
                    } else {
                        itemAdapter.addParticipantToGame(game,user.getUid(), getApplicationContext());
                        Log.d(LOG_TAG,"buttonParticipate clicked, user found, id: " + user.getUid());
                        buttonParticipate.setEnabled(false);
                        MainActivity.setAllGamesIsCurrentView(false);
                        startActivity(new Intent(GameDetails.this,MainActivity.class));
                    }
                }
            }
        });

        buttonResign = findViewById(R.id.button_resign);
        buttonResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null){
                    Log.d(LOG_TAG,"buttonResign clicked, no user found");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(mAuth.getCurrentUser().getUid())){
                        itemAdapter.removeParticipantFromGame(game,user.getUid(), getApplicationContext(),getString(R.string.toast_removedYou));
                        Log.d(LOG_TAG,"buttonResign clicked, user found, id: " + user.getUid());
                        buttonResign.setEnabled(false);
                        MainActivity.setAllGamesIsCurrentView(false);
                        startActivity(new Intent(GameDetails.this,MainActivity.class));
                    } else {
                        Log.d(LOG_TAG,"should never get to this place");
                    }

                }
            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("GameDetails","entered onAuthStateChanged!");
                if (mAuth.getCurrentUser() != null){
                    //if user has already signed up for this game
                    if (game.getParticipants().contains(mAuth.getCurrentUser().getUid())){
                        buttonParticipate.setEnabled(false);

                    } else {
                        buttonResign.setEnabled(false);
                    }
                } else {
                    buttonParticipate.setEnabled(true);
                    buttonResign.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.games_nearby)  {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        if(id == R.id.account)  {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(this,AccountActivity.class));
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }
        if (id == R.id.newGame)  {
            Intent intent = new Intent(this,NewGameActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
