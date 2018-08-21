package de.ur.mi.android.sportsfreund;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DebugActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        textView = findViewById(R.id.debug_textview);
        if (Constants.debugText != null){
            textView.setText(Constants.debugText);
        }
    }
}
