package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //ActionBar zum umschalten zwischen Teilgenommenen und Spielen in der Nähe
    ActionBar actionBar;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);

        //Button für neues Spiel erstellen

        newGameButton = (Button)findViewById(R.id.button_new_game);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToNewGame();
            }
        });
    }

    private void changeToNewGame() {

        Intent i = new Intent(this, NewGame.class);

        startActivity(i);
    }


    //Instanziieren des Action menüs
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //On selected Methode für MenuiTEMS

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signedIn:
                actionBar.setTitle(R.string.games_signd_in);
                sortByProximity();
                populateList();

                break;
            case R.id.nearby:
                actionBar.setTitle(R.string.games_nearby);
                getSignedInGames();
                populateList();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByProximity()  {
        // Sortieren der Spiele aus Firebase
    }

    private void getSignedInGames()  {
        //Auswahl der angemeldeten spiele aus localer Datenbank
    }

    private void populateList()  {
        //Erzeugen des Listview
    }

}
