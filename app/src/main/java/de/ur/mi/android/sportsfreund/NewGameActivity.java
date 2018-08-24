package de.ur.mi.android.sportsfreund;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class NewGameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String LOG_TAG = "NewGameActivity";

    private EditText inputGame;
    private static Button inputTime;
    private static Button inputDate;
    Button makeGameButton;
    Button mapButton;
    private TextView locationSet;
    final int REQUEST_CODE = 1;
    private Location gameLocation;
    private String noLocation= "no Location found";
    public static final String KEY_LOCATION_LAT= "lKeyLat";
    public static final String KEY_LOCATION_LONG = "lKeyLong";

    double locLat;
    double locLong;

    static String gameTime;
    static String gameDate;


    private ItemAdapter_neu itemAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private android.support.v7.app.ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.games_nearby);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        Log.d("bla","currentUser: " +currentUser);

        inputGame = findViewById(R.id.input_name);
        inputDate = findViewById(R.id.input_date);
        inputTime = findViewById(R.id.input_time);

        setupMapButton();
        setupCreateGameButton();
        setupDateButton();
        setupTimeButton();
        //setupTextView();
        itemAdapter = MainActivity.getItemAdapter();

        mDrawerLayout = findViewById(R.id.newGameDrawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void setupDateButton() {
        inputDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        } );
    }

    private void setupTimeButton() {
        inputTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        } );
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))  {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupCreateGameButton() {
            makeGameButton = findViewById(R.id.button_make_new_game);
            makeGameButton.setEnabled( false );
        makeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewGame();
            }
        });
    }

    private void makeNewGame() {
        if (inputGame.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bitte gib einen Namen für das Spiel an!", Toast.LENGTH_SHORT).show();
        } else if (dateTimeIsHistory()) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_enterFutureDateTime), Toast.LENGTH_SHORT).show();
        } else if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        } else {
            String gameName = inputGame.getText().toString();

            Game game = new Game(getApplicationContext(), gameName, gameDate, gameTime, locLat, locLong, currentUser.getUid());
            itemAdapter.addGameToDatabase(game, this);
            MainActivity.setAllGamesIsCurrentView(false);
            Intent backToMainWithMyGames = new Intent(this, MainActivity.class);
            startActivity(backToMainWithMyGames);
        }
    }

    private Boolean dateTimeIsHistory(){
        final Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String currentMonthString = Integer.toString(currentMonth);
        if (currentMonth < 10){
            currentMonthString = "0" + currentMonthString;
        }
        String currentDate = c.get(Calendar.YEAR) + "-" + currentMonthString + "-" + c.get(Calendar.DAY_OF_MONTH);
        Log.d(LOG_TAG,"currentDate: " + currentDate);
        if (gameDate.compareTo(currentDate) < 0){
            return true;
        } else if (gameDate.compareTo(currentDate) > 0){
            return false;
        } else {
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            int currentMinute = c.get(Calendar.MINUTE);
            String hourString = Integer.toString(currentHour);
            if (currentHour < 10){
                hourString = "0" + hourString;
            }
            String minuteString = Integer.toString(currentMinute);
            if (currentMinute < 10){
                minuteString = "0" + minuteString;
            }
            String currentTimeString = hourString + ":" + minuteString;
            Log.d(LOG_TAG,"currentTimeString: " + currentTimeString);

            if (gameTime.compareTo(currentTimeString) <= 0){
                return true;
            } else {
                return false;
            }
        }

    }

    /*
    private void makeNewGame() {
        String gameName = inputGame.getText().toString();
        String gameTime = inputTime.getText().toString();
        double gameLat = locLat;
        double gameLong = locLong;
        Game game = new Game(gameName,gameTime,gameLat,gameLong,uid);
        new RealtimeSync().execute(game);

    }
    */
    /*
    private void addGameToDatabase(Game game){
        DatabaseReference mDatabaseGames = FirebaseDatabase.getInstance().getReference("games");
        String gameId = mDatabaseGames.push().getKey();
        mDatabaseGames.child(gameId).setValue(game,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if (databaseError != null){
                    Toast.makeText(getApplicationContext(),toastAddGameFailed,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),toastGameAdded,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    */

    /*
    private void setupTextView() {

        locationSet = findViewById( R.id.location_set );
        locationSet.setText("dummy");
    }
    */

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

        Intent getLocation = new Intent(this, MapsActivity.class);

        startActivityForResult(getLocation,REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode==REQUEST_CODE){
        if(resultCode== Activity.RESULT_OK){

           Bundle extras = data.getExtras();
            locLat = data.getDoubleExtra(KEY_LOCATION_LAT, 1111111111);
            locLong = data.getDoubleExtra(KEY_LOCATION_LONG, 11111111);

            makeGameButton.setEnabled( true );
            mapButton.setText( "Ort festgelegt, über GoogleMaps ändern?"  );
            if (locLat != 0){
                Log.d("NewGameActivity","LocLat: " + locLat);
                Log.d("NewGameActivity", "LocLong" + locLong);
            }
    }}}


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.account)  {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null){
                startActivity(new Intent(this,AccountActivity.class));
            } else {
                startActivity(new Intent(this,SignUpActivity.class));
            }
        }

        if(id == R.id.games)  {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return false;
    }


    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentManager fragMan = getFragmentManager();

        newFragment.show(fragMan, "timePicker");

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fragMan = getFragmentManager();

        newFragment.show(fragMan, "datePicker");
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            String stunden = String.valueOf(hourOfDay);
            if (hourOfDay < 10) {
                stunden = "0" + hourOfDay;
            }

            String minuten = String.valueOf(min);
            if (min < 10) {
                minuten = "0" + minuten;
            }

            gameTime = stunden +":"+ minuten;
            inputTime.setText( "Erledigt, Klicke um "+gameTime+ " Uhr zu ändern"  );
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String yearAsString = Integer.toString(year);
            //january is represented with 0, therefore +1
            String monthAsString = Integer.toString(month + 1);
            String dayAsString = Integer.toString(day);
            if (month < 9){
                monthAsString = "0" + monthAsString;
            }
            if (day < 10){
                dayAsString = "0" + dayAsString;
            }
            gameDate = yearAsString + "-" + monthAsString + "-" + dayAsString;

            Log.d("NewGameDate",gameDate);
            inputDate.setText( "Erledigt, Klicke um " + gameDate + " zu ändern"  );
        }
    }
}

