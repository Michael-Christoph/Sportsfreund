package de.ur.mi.android.sportsfreund;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView type,time,date,description,desTitle;
    Button showLocation;
    Button participate;
    Button resign;
    ListView listView;

    private ItemAdapter_neu itemAdapter;
    private Game game;

    FirebaseAuth auth;
    public static final String KEY_LOCATION_LAT_D= "lKeyLatDetails";
    public static final String KEY_LOCATION_LONG_D = "lKeyLongDetails";
    public static final String KEY_GAME_NAME = "GameName";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        auth = FirebaseAuth.getInstance();
        itemAdapter = MainActivity.getItemAdapter();

        mDrawerLayout = findViewById(R.id.detailsDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

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
        date.setText(game.getDate());



        showLocation = findViewById(R.id.showOnMaps);

        TextView headerView = new TextView(getApplicationContext());
        headerView.setText(R.string.participants);
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        headerView.setAllCaps(true);


        setupParticipateAndResignButtons();

        //time.setText(getIntent().getStringExtra("title"));
        //date.setText(getIntent().getStringExtra("body"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationonMaps ();

            }
        });



    }

    private void showLocationonMaps() {
            Intent showGameLocation = new Intent(this, MapsActivity.class);

        double locLong = game.getGameLong();
        double locLat = game.getGameLat();

        showGameLocation.putExtra(KEY_LOCATION_LAT_D, locLat);
        showGameLocation.putExtra( KEY_LOCATION_LONG_D, locLong );
        showGameLocation.putExtra( KEY_GAME_NAME, game.getGameName() );

            startActivity(showGameLocation);

    }

    private void setupParticipateAndResignButtons(){
        participate = findViewById(R.id.button_participate);

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    Log.d("bla","participate clicked, no user found ");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(auth.getCurrentUser().getUid())){
                        Log.d("bla","participate-onClick-else-if: should never get to this place");
                    } else {
                        itemAdapter.addParticipantToGame(game,user.getUid(), getApplicationContext());
                        Log.d("bla","participate clicked, user found, id: " + user.getUid());
                        participate.setEnabled(false);
                        MainActivity.setAllGamesIsCurrentView(false);
                        startActivity(new Intent(GameDetails.this,MainActivity.class));
                    }
                }
            }
        });

        resign = findViewById(R.id.button_resign);

        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    Log.d("bla","resign clicked, no user found");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(auth.getCurrentUser().getUid())){
                        itemAdapter.removeParticipantFromGame(game,user.getUid(), getApplicationContext());
                        Log.d("bla","resign clicked, user found, id: " + user.getUid());
                        resign.setEnabled(false);
                        MainActivity.setAllGamesIsCurrentView(false);
                        startActivity(new Intent(GameDetails.this,MainActivity.class));
                    } else {
                        Log.d("bla","should never get to this place");
                    }

                }
            }
        });

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("GameDetails","entered onAuthStateChanged!");
                if (auth.getCurrentUser() != null){
                    //if user has already signed up for this game
                    if (game.getParticipants().contains(auth.getCurrentUser().getUid())){
                        participate.setEnabled(false);

                    } else {
                        resign.setEnabled(false);
                    }
                } else {
                    participate.setEnabled(true);
                    resign.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.games)  {
            openActivity(MainActivity.class);
        }

        if(id == R.id.acount)  {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(this,AccountActivity.class));
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }

        if (id == R.id.newGame)  {
            openActivity(NewGame.class);
        }
        return false;
    }

    private void openActivity(Class activity)  {
        Intent intent = new Intent(this,activity);
        startActivity(intent);

    }
}
