package de.ur.mi.android.sportsfreund;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private static boolean allGamesIsCurrentView = true;
    ArrayList<Game> gamesForCurrentView = new ArrayList<>();
    ArrayList<Game> gamesInDatabase = new ArrayList<>();

    private FirebaseAuth auth;

    private static ItemAdapter_neu itemAdapter;
    ListView listView;
    FloatingActionButton fab;
    private final int LOCATION_REQUEST_CODE = 2;

    private static final String LOG_TAG = "MyMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG,"entered onCreate");

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);
        mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE);


    }


    private void finishSetup()  {

        setupAdapterAndListView();

        itemAdapter.renewViewAccordingToActionBar();

        //nur fuer MP zum Testen
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeToNewGame();
                /*
                String gameKey = "-LJsF-e5rkJAQGi4PxmS";
                auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null){
                    MainActivity.getItemAdapter().removeParticipantFromGameViaKey(gameKey,auth.getCurrentUser().getUid());
                } else {
                    Log.d("Receiver","user is null");
                    //Alert, dass leider keine Abmeldung möglich; User möge sich bitte zuerst anmelden
                    // und dann manuell vom Spiel abmelden.
                }
                */
            }
        });
        tellAboutGps( getApplicationContext() );
    }

    private void tellAboutGps(Context context) {
        boolean gpsEnabled= NavigationController.getInstance(context).gpsIsEnabled();
        if ( gpsEnabled == false )
        {
            Toast.makeText(context, "GPS ist ausgeschaltet, daher kann Entfernung nicht angezeigt werden!", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "GPS ist eingeschaltet!", Toast.LENGTH_LONG).show();
        }
    }


    private void requestPermissions(String permission, int requestCode)  {
        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        } else {
            finishSetup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)  {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)  {
                    finishSetup();
                }
                else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setTitle(R.string.gps_title);
                    dialogBuilder.setMessage(R.string.gps_message);
                    dialogBuilder.setPositiveButton(R.string.positive_button_text, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissions,LOCATION_REQUEST_CODE);
                            }
                            //Timo hat ursprgl geschrieben:
                            //requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE);
                        }
                    });

                    dialogBuilder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
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
        listView = findViewById(R.id.listView);
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

    private void changeToNewGame() {
        Intent i = new Intent(this, NewGameActivity.class);
        startActivity(i);
    }

    private void showGame(Game game)  {
        Log.d(LOG_TAG,"gameName: " + game.getGameName());
        Log.d(LOG_TAG,"gameDate: " + game.getGameDate());
        Log.d(LOG_TAG,"gameTime: " + game.getGameTime());
        Intent intent = new Intent(this,GameDetails.class);
        intent.putExtra("game",game);
        //intent.putExtra("title",title);
        //intent.putExtra("body",body);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        Log.d(LOG_TAG,"entered onRestart");

        super.onRestart();
    }

    //is being called after onCreate or after onRestart
    @Override
    protected void onStart(){
        Log.d(LOG_TAG,"entered onStart");

        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE);

        //itemAdapter.renewViewAccordingToActionBar();
        setActionBarAccordingToView();

        super.onStart();
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

        int id = item.getItemId();


        itemAdapter.renewViewAccordingToActionBar();
        if(id == R.id.nearby) {
            if (allGamesIsCurrentView) {
                Toast.makeText(getApplicationContext(), "all Games", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "my Games", Toast.LENGTH_SHORT).show();
            }
        }


            if(mToggle.onOptionsItemSelected(item))  {
                return true;
            }


        /*
        switch (item.getItemId()) {
            case R.id.signedIn:
                actionBar.setTitle(R.string.games_signd_in);
                allGamesIsCurrentView = false;

                itemAdapter.renewViewAccordingToActionBar();
                setupAdapterAndListView();
                break;
            case R.id.nearby:
                actionBar.setTitle(R.string.games_nearby);
                allGamesIsCurrentView = true;

                itemAdapter.renewViewAccordingToActionBar();
                setupAdapterAndListView();
                break;


        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.account) {
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
            mDrawerLayout.closeDrawers();
            allGamesIsCurrentView = true;
            setActionBarAccordingToView();
            itemAdapter.renewViewAccordingToActionBar();
        }
        if(id == R.id.myGames) {
            if (auth.getCurrentUser() != null) {
                mDrawerLayout.closeDrawers();
                allGamesIsCurrentView = false;
                setActionBarAccordingToView();
                itemAdapter.renewViewAccordingToActionBar();
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }

        }
        if (id == R.id.debug_item){
            startActivity(new Intent(this,DebugActivity.class));
        }
        if (id == R.id.simulation){
            startActivity(new Intent(this,SimulationActivity.class));
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
        itemAdapter.renewViewAccordingToActionBar();
    }
    private void setActionBarAccordingToView(){
        if (allGamesIsCurrentView){
            actionBar.setTitle(R.string.games_nearby);
        } else {
            actionBar.setTitle(R.string.games_signed_in);
        }
    }
}
