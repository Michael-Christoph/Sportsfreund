package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //ActionBar zum umschalten zwischen Teilgenommenen und Spielen in der Nähe
    ActionBar actionBar;

    private static boolean allGamesIsCurrentView = true;
    private Button newGameButton;
    ArrayList<Game> gamesForCurrentView = new ArrayList<>();
    ArrayList<Game> gamesInDatabase = new ArrayList<>();

    private FirebaseAuth auth;



    private static ItemAdapter_neu itemAdapter;
    ListView listView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MP: adds offline persistence even if app is destroyed.
        if (savedInstanceState == null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);

        //getGamesFromDatabase_dummy();
        setupAdapterAndListView();
        //getGamesFromDatabase();

        sortGamesFromDatabaseByProximity();


        //nur fuer MP zum Testen
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemAdapter.add(new Game("test","test","Lat: 42.424242424242424","test"));
            }
        });

        //Button für neues Spiel erstellen


        /*
        //bloss fuer MP zum Testen
        Button testRegisterButton = findViewById(R.id.register_test_button);
        testRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    private void setupAdapterAndListView() {
        itemAdapter = new ItemAdapter_neu(this,gamesForCurrentView);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSingleGameActivity();
                Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGamesFromDatabase_dummy() {
        Game game1 = new Game("Testgame1","Testtime1","Testlocation1","egalid");
        Game game2 = new Game("Testgame2","Testtime2","Testlocation2","egalid2");
        gamesForCurrentView.add(game1);
        gamesForCurrentView.add(game2);
    }

    /*
    private void getGamesFromDatabase(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //itemAdapter.clear();
                gamesInDatabase.clear();
                for (DataSnapshot gameDataSnapshot: dataSnapshot.getChildren()){
                    Game game = gameDataSnapshot.getValue(Game.class);
                    gamesInDatabase.add(game);
                    //itemAdapter.add(game);
                }
                renewViewAccordingToActionBar();
                //MP: didn't work for some reason, so I decided to add the games directly to the
                //itemAdapter, not to the ArrayList (in the for loop above)
                //itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Failed to " +
                        "read database: " + databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    private void renewViewAccordingToActionBar() {
        if (allGamesIsCurrentView){
            sortGamesFromDatabaseByProximity();
        //if current view is "signed in games" only
        } else {
            filterSignedInGamesAndSortByTime();
        }
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
                allGamesIsCurrentView = false;
                filterSignedInGamesAndSortByTime();
                break;
            case R.id.nearby:
                actionBar.setTitle(R.string.games_nearby);
                allGamesIsCurrentView = true;
                sortGamesFromDatabaseByProximity();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void sortGamesFromDatabaseByProximity()  {
        // Sortieren der Spiele aus Firebase
        Collections.sort(gamesInDatabase, new Comparator<Game>() {
            @Override
            public int compare(Game game, Game t1) {
                double proximityGame = game.getProximity(NavigationHelperDummy.getLastKnownLocation());
                Log.d("bla","proxmityGame: " + proximityGame);
                double proximityt1 = t1.getProximity(NavigationHelperDummy.getLastKnownLocation());
                Log.d("bla","proximityt1: " + proximityt1);
                int comparisonResult = Double.compare(proximityGame,proximityt1);
                Log.d("bla","comparisonResult: " + comparisonResult);
                return comparisonResult;
            }
        });
        gamesForCurrentView = gamesInDatabase;
        itemAdapter.clear();
        for (Game game: gamesForCurrentView){
            itemAdapter.add(game);
        }
    }

    private void filterSignedInGamesAndSortByTime()  {

    }

    private void openSingleGameActivity() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.acount) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(MainActivity.this,AccountActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }

        }
        if(id == R.id.newGame) {
            changeToNewGame();
        }
        if(id == R.id.localGames) {
            actionBar.setTitle(R.string.games_nearby);
            sortGamesFromDatabaseByProximity();
            Toast.makeText(this, "localGames", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.myGames) {
            actionBar.setTitle(R.string.games_signd_in);
            filterSignedInGamesAndSortByTime();
            Toast.makeText(this, "myGames", Toast.LENGTH_SHORT).show();

        }

        return false;
    }
    public static ItemAdapter_neu getItemAdapter() {
        return itemAdapter;
    }
    public static boolean allGamesIsCurrentView() {
        return allGamesIsCurrentView;
    }
}
