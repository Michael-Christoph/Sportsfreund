package de.ur.mi.android.sportsfreund;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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

import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //ActionBar zum umschalten zwischen Teilgenommenen und Spielen in der Nähe
    ActionBar actionBar;

    private static boolean allGamesIsCurrentView = true;
    ArrayList<Game> gamesForCurrentView = new ArrayList<>();
    ArrayList<Game> gamesInDatabase = new ArrayList<>();

    private FirebaseAuth auth;

    private static ItemAdapter_neu itemAdapter;
    ListView listView;
    FloatingActionButton fab;
    private final int LOCATION_REQUEST_CODE = 2;

    private ActionBarDrawerToggle mToggle;

    private String gpsMessage = "Du musst den Zugriff auf den Standort erlauben um Sportsfreund richtig nutzen zu können.";
    private  String gpsTitle = "Achtung";
    private String positiveButtonText = "Erlauben";
    private String negativeButtonText = "Weiter ohne GPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);

        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE);


    }

    private void finishSetup()  {
        //getGamesFromDatabase_dummy();
        setupAdapterAndListView();
        //getGamesFromDatabase();

        sortGamesFromDatabaseByProximity();


        //nur fuer MP zum Testen
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game = new Game(getApplicationContext(),"Testspiel","06.08.2018","23:59",42.424242424242424,22.424242424242424,"Testid");
                itemAdapter.add(game,MainActivity.this);
            }
        });

    }

    private void requestPermissions(String permission, int requestCode)  {
        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);

        }
        else  {
            finishSetup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)  {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)  {
                    finishSetup();
                }
                else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setTitle(gpsTitle);
                    dialogBuilder.setMessage(gpsMessage);
                    dialogBuilder.setPositiveButton(positiveButtonText, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE);
                        }
                    });

                    dialogBuilder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishSetup();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupAdapterAndListView() {
        itemAdapter = new ItemAdapter_neu(this,gamesForCurrentView);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(itemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("bla","onItemClick funktioniert");
                Game game = itemAdapter.getItem(position);
                showGame(game);
            }
        });
    }

    /*
    private void getGamesFromDatabase_dummy() {
        Game game1 = new Game("Testgame1","Testtime1","Testlocation1","egalid");
        Game game2 = new Game("Testgame2","Testtime2","Testlocation2","egalid2");
        gamesForCurrentView.add(game1);
        gamesForCurrentView.add(game2);
    }
    */

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

    private void showGame(Game game)  {
        Intent intent = new Intent(this,GameDetails.class);
        intent.putExtra("game",game);
        //intent.putExtra("title",title);
        //intent.putExtra("body",body);
        startActivity(intent);
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
                double proximityGame = game.distanceToGame(getApplicationContext());
                Log.d("bla","proxmityGame: " + proximityGame);
                double proximityt1 = t1.distanceToGame(getApplicationContext());
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

    /*
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
                Game game = array[position];
                showGame(game.getGameName(),game.getGameTime());

            }
        });
        gamesForCurrentView = gamesInDatabase;
        itemAdapter.clear();
        for (Game game: gamesForCurrentView){
            itemAdapter.add(game);
        }
    }
    */

    private void filterSignedInGamesAndSortByTime()  {

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
    public static void setAllGamesIsCurrentView(boolean allGamesIsCurrentView) {
        MainActivity.allGamesIsCurrentView = allGamesIsCurrentView;
    }
}
