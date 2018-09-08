package de.ur.mi.android.sportsfreund;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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

    private static final String LOG_TAG = "MyMainActivity";
    private final int LOCATION_REQUEST_CODE = 2;
    private static ItemAdapter itemAdapter;
    private static boolean allGamesIsCurrentView = true;

    ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    ArrayList<Game> gamesForCurrentView = new ArrayList<>();

    private FirebaseAuth auth;

    private ListView listView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG,"entered onCreate");

        NavigationView navigationView = findViewById(R.id.navigation_view);
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

        finishSetup();

        //MainActivity has to be started with extras when notification interaction is used
        if (getIntent().getExtras() != null){
            if (getIntent().getExtras().getString(GlobalVariables.KEY_GAME_TO_DELETE) != null){
                String keyGameToDelete = getIntent().getExtras().getString(GlobalVariables.KEY_GAME_TO_DELETE);
                String userId = getIntent().getExtras().getString(GlobalVariables.KEY_USER_ID);
                itemAdapter.removeParticipantFromGameViaKey(keyGameToDelete,userId,this,getString(R.string.toast_removedYou));
                boolean moveTaskToBack = getIntent().getExtras().getBoolean(GlobalVariables.KEY_MOVE_TASK_TO_BACK);
                if (moveTaskToBack){
                    moveTaskToBack(true);
                }
            }
        }
    }
    private void finishSetup()  {
        setupAdapterAndListView();
        itemAdapter.renewViewAccordingToSelectedView();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToNewGame();
            }
        });

        //informs user if gps is not turned on
        tellAboutGps(getApplicationContext());
    }

    private void tellAboutGps(Context context) {
        boolean gpsEnabled= NavigationController.getInstance(context).isGpsEnabled();
        if (gpsEnabled == false) {
            Toast.makeText(context, "GPS ist ausgeschaltet, daher kann Entfernung nicht angezeigt werden!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions(String permission, int requestCode)  {
        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)  {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)  {
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
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //finishSetup();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupAdapterAndListView() {
        itemAdapter = new ItemAdapter(this,gamesForCurrentView);
        listView = findViewById(R.id.listView);
        listView.setAdapter(itemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG,"onItemClick funktioniert");
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
        Intent intent = new Intent(this,GameDetails.class);
        intent.putExtra("game",game);
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
        setActionBarAccordingToView();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.refresh) {
            itemAdapter.renewViewAccordingToSelectedView();
            Toast.makeText(this,getString(R.string.toast_refreshed),Toast.LENGTH_SHORT).show();
            if (itemAdapter.getGamesInDatabase().get(0).distanceToGame(this) == null){
                Toast.makeText(this,getString(R.string.toast_locationNotFound),Toast.LENGTH_SHORT).show();
            }
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                itemAdapter.setShowNoGps(true);
            } else {
                itemAdapter.setShowNoGps(false);
            }
        }

        if(mToggle.onOptionsItemSelected(item))  {
            return true;
        }

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
            itemAdapter.renewViewAccordingToSelectedView();
        }
        if(id == R.id.myGames) {
            if (auth.getCurrentUser() != null) {
                mDrawerLayout.closeDrawers();
                allGamesIsCurrentView = false;
                setActionBarAccordingToView();
                itemAdapter.renewViewAccordingToSelectedView();
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }
        if (id == R.id.simulation){
            startActivity(new Intent(this,SimulationActivity.class));
        }

        return false;
    }
    public static ItemAdapter getItemAdapter() {
        return itemAdapter;
    }
    public static boolean allGamesIsCurrentView() {
        return allGamesIsCurrentView;
    }
    public static void setAllGamesIsCurrentView(boolean allGamesIsCurrentView) {
        MainActivity.allGamesIsCurrentView = allGamesIsCurrentView;
        itemAdapter.renewViewAccordingToSelectedView();
    }
    private void setActionBarAccordingToView(){
        if (allGamesIsCurrentView){
            actionBar.setTitle(R.string.games_nearby);
        } else {
            actionBar.setTitle(R.string.games_signed_in);
        }
    }
}
