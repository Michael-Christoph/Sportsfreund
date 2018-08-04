package de.ur.mi.android.sportsfreund;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameDetails extends AppCompatActivity  {

    TextView time,date,description;
    Button showLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        setup();
    }

    private void setup()  {
        time = findViewById(R.id.time_data);
        date = findViewById(R.id.date_data);
        description = findViewById(R.id.description_data);
        showLocation = findViewById(R.id.showOnMaps);
        Button showOnMaps = findViewById(R.id.showOnMaps);

        time.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("body"));

        showOnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
