package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameDetails extends AppCompatActivity  {

    TextView type,time,date,description;
    Button showLocation;
    Button participate;
    Button resign;
    ListView listView;

    private ItemAdapter_neu itemAdapter;
    private Game game;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);
        auth = FirebaseAuth.getInstance();
        itemAdapter = MainActivity.getItemAdapter();

        setup();
    }

    private void setup()  {
        Intent intent = getIntent();
        game = (Game) intent.getSerializableExtra("serializable");
        type = findViewById(R.id.type);
        type.setText(game.getGameName());
        time = findViewById(R.id.time_data);
        time.setText(game.getGameTime());
        date = findViewById(R.id.date_data);
        date.setText(game.getDate());
        description = findViewById(R.id.description_data);

        showLocation = findViewById(R.id.showOnMaps);
        listView = findViewById(R.id.gameDetails_listView);
        TextView headerView = new TextView(getApplicationContext());
        headerView.setText(R.string.participants);
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        headerView.setAllCaps(true);
        listView.addHeaderView(headerView);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                android.R.id.text1,game.getParticipants());
        listView.setAdapter(aa);

        setupParticipateAndResignButtons();

        //time.setText(getIntent().getStringExtra("title"));
        //date.setText(getIntent().getStringExtra("body"));

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    private void setupParticipateAndResignButtons(){
        participate = findViewById(R.id.button_participate);
        /*
        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    itemAdapter.addParticipantToGame(game,user.getUid());
                    MainActivity.setAllGamesIsCurrentView(false);
                    startActivity(new Intent(GameDetails.this,MainActivity.class));
                }
            }
        });
        */
        resign = findViewById(R.id.button_resign);
        /*
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null){
                    startActivity(new Intent(GameDetails.this,SignUpActivity.class));
                } else {
                    itemAdapter.removeParticipantFromGame(game,user.getUid());
                    MainActivity.setAllGamesIsCurrentView(false);
                    startActivity(new Intent(GameDetails.this,MainActivity.class));
                }
            }
        });
        */

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
        };
    }
}
