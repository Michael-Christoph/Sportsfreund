package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GameDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView type,time,date,textViewParticipants;
    private String participantsString = "";
    Button showLocation;
    Button buttonParticipate;
    Button buttonResign;
    //ListView listView;

    private ItemAdapter_neu itemAdapter;
    private Game game;

    FirebaseAuth auth;
    public static final String KEY_LOCATION_LAT_D= "lKeyLatDetails";
    public static final String KEY_LOCATION_LONG_D = "lKeyLongDetails";
    public static final String KEY_GAME_NAME = "GameName";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private String toastParticipantRemoved = "Du bist jetzt von dem Spiel abgemeldet";


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
        Log.d("GameDetails","gameName: " + game.getGameName());
        Log.d("GameDetails","gameDate: " + game.getGameDate());
        Log.d("GameDetails","gameTime: " + game.getGameTime());
        type = findViewById(R.id.title);
        type.setText(game.getGameName());
        time = findViewById(R.id.time);
        time.setText(game.getGameTime());
        date = findViewById(R.id.date);
        date.setText(game.getGameDate());

        textViewParticipants = findViewById(R.id.participants);
        String addYou = "";
        for (String participantId: game.getParticipants()){
            /*
            if (participantId.equals(auth.getCurrentUser().getUid())){
                addYou = " und du!";
            } else {
            */
                participantsString += "\u26F9";
            //}
        }
        if (!addYou.equals("")){
            participantsString += addYou;
        }
        textViewParticipants.setText(participantsString);

        /*
        listView = findViewById(R.id.gameDetails_listView);
        TextView headerView = new TextView(getApplicationContext());
        headerView.setText(R.string.gameDetails_participantsHeader);
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        headerView.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
        headerView.setAllCaps(true);
        headerView.setGravity(Gravity.CENTER);
        listView.addHeaderView(headerView);
        ArrayList<String> participantsForAdapter = new ArrayList<>();
        for (String participantId: game.getParticipants()){
            String newParticipantId = participantId;
            if (participantId.equals(auth.getCurrentUser().getUid())){
                newParticipantId = getString(R.string.gameDetails_participantYou);
            }
            participantsForAdapter.add(newParticipantId);
        }
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                android.R.id.text1,participantsForAdapter);
        listView.setAdapter(aa);
        */


        setupParticipateAndResignButtons();

        showLocation = findViewById(R.id.showOnMaps);
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationOnMaps();

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void showLocationOnMaps() {
        Intent showGameLocation = new Intent(this, MapsActivity.class);
        double locLong = game.getGameLong();
        double locLat = game.getGameLat();
        showGameLocation.putExtra(KEY_LOCATION_LAT_D, locLat);
        showGameLocation.putExtra( KEY_LOCATION_LONG_D, locLong );
        showGameLocation.putExtra( KEY_GAME_NAME, game.getGameName() );
        startActivity(showGameLocation);
    }

    private void setupParticipateAndResignButtons(){
        buttonParticipate = findViewById(R.id.button_participate);
        buttonParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    Log.d("bla","buttonParticipate clicked, no user found ");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(auth.getCurrentUser().getUid())){
                        Log.d("bla","buttonParticipate-onClick-else-if: should never get to this place");
                    } else {
                        itemAdapter.addParticipantToGame(game,user.getUid(), getApplicationContext());
                        Log.d("bla","buttonParticipate clicked, user found, id: " + user.getUid());
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
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    Log.d("bla","buttonResign clicked, no user found");
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    if (game.getParticipants().contains(auth.getCurrentUser().getUid())){
                        itemAdapter.removeParticipantFromGame(game,user.getUid(), getApplicationContext(),toastParticipantRemoved);
                        Log.d("bla","buttonResign clicked, user found, id: " + user.getUid());
                        buttonResign.setEnabled(false);
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

        if (id == R.id.games)  {
            openActivity(MainActivity.class);
        }

        if(id == R.id.account)  {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(this,AccountActivity.class));
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }

        if (id == R.id.newGame)  {
            openActivity(NewGameActivity.class);
        }
        return false;
    }

    private void openActivity(Class activity)  {
        Intent intent = new Intent(this,activity);
        startActivity(intent);

    }
}
