package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SimulationActivity extends AppCompatActivity {

    TextView textView1,textView2;
    Button btn1,btn2,btn3,btn4;
    ItemAdapter_neu itemAdapter;

    Game testGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        itemAdapter = MainActivity.getItemAdapter();
        Log.d("Simulation: ","itemAdapter = " + itemAdapter.toString());

        textView1 = findViewById(R.id.simulation_text_1);
        btn1 = findViewById(R.id.simulation_button_1);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBtn1Clicked();
            }
        });
        btn2 = findViewById(R.id.simulation_button_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtn2Clicked();
            }
        });
        btn2.setEnabled(false);
        textView2 = findViewById(R.id.simulation_text_2);
        btn3 = findViewById(R.id.simulation_button_3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtn3Clicked();
            }
        });
        btn3.setEnabled(false);
        btn4 = findViewById(R.id.simulation_button_4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtn4Clicked();
            }
        });
        btn4.setEnabled(false);

    }
    private void onBtn1Clicked(){
        Log.d("Simulation: ","entered onBtn1Clicked");
        //testGame is furthest away from German user with respect to date and time
        Game testGame = new Game(getApplicationContext(),"Testspiel","4242-11-11","23:59",-45.902796,-177.323183,"testuserid");
        Log.d("Simulation: ","testGame = " + testGame.getGameName());
        itemAdapter.addGameToDatabase(testGame,getApplicationContext());
        btn2.setEnabled(true);
        btn1.setEnabled(false);
    }
    private void onBtn2Clicked(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
        } else {
            testGame = itemAdapter.getItem(0);
            itemAdapter.addParticipantToGame(testGame,user.getUid(),getApplicationContext());
            textView2.setText(getString(R.string.simulation_explanation_2));
            btn2.setEnabled(false);
            btn3.setEnabled(true);

        }

    }
    private void onBtn3Clicked(){
        itemAdapter.addParticipantToGame(testGame,"testuserid2",getApplicationContext());
        btn3.setEnabled(false);
        btn4.setEnabled(true);
    }
    private void onBtn4Clicked(){
        itemAdapter.removeParticipantFromGame(testGame,"testuserid2",getApplicationContext(),"Der zufällige Teilnehmer wurde vom Spiel abgemeldet");
        btn4.setEnabled(false);
    }
    @Override
    public void finish(){
        itemAdapter.remove(testGame,getApplicationContext());
        super.finish();
    }

}
