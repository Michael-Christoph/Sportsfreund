package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewGame extends AppCompatActivity {
    private Button mapButton;
    private Button makeGameButton;
    private EditText inputGame;
    private EditText inputTime;


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

