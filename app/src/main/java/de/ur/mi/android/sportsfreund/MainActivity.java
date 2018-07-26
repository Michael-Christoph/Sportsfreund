package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    //ActionBar zum umschalten zwischen Teilgenommenen und Spielen in der Nähe
    ActionBar actionBar;
    private Button newGameButton;
    String[] games = {"Game1","game2","game3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);
        sortByProximity();
        populateList();

        //Button für neues Spiel erstellen

        newGameButton = (Button)findViewById(R.id.button_new_game);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToNewGame();
            }
        });

        //bloss fuer MP zum Testen
        Button testRegisterButton = findViewById(R.id.register_test_button);
        testRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
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

    //On selected Methode für MenuItems

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
        TextView textView = findViewById(R.id.textview);







    }

    private void getSignedInGames()  {
        //Auswahl der angemeldeten spiele aus localer Datenbank
    }

    private void populateList()  {

        ListAdapter listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,games);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSingleGameActivity();
                Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openSingleGameActivity() {

    }

}
