package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //ActionBar zum umschalten zwischen Teilgenommenen und Spielen in der Nähe
    ActionBar actionBar;
    private Button newGameButton;
    String[] games = {"Game1","game2","game3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);
        sortByProximity();
        populateList();

        //Button für neues Spiel erstellen



        //bloss fuer MP zum Testen
        /*Button testRegisterButton = findViewById(R.id.register_test_button);
        testRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        */
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


    }

    private void getSignedInGames()  {
        //Auswahl der angemeldeten spiele aus localer Datenbank
    }

    private void populateList()  {


        //Erstellen eines beispiel Arrays
        ListObject[] array = new ListObject[10];
        for (int i = 0; i<10; i++)  {
            ListObject object = new ListObject("Spiel "+i,"Beschreibung "+i);
            array[i] = object;
        }


        ListAdapter listAdapter = new ItemAdapter(this,array);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.acount) {
            Toast.makeText(this, "account", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.newGame) {
            changeToNewGame();
        }
        if(id == R.id.localGames) {
            actionBar.setTitle(R.string.games_nearby);
            sortByProximity();
            populateList();
            Toast.makeText(this, "localGames", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.myGames) {
            actionBar.setTitle(R.string.games_signd_in);
            getSignedInGames();
            populateList();
            Toast.makeText(this, "myGames", Toast.LENGTH_SHORT).show();

        }

        return false;
    }
}
