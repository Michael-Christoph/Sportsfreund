package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String userIdOfLastUser = "";

    private TextView textViewTop;
    private TextView textViewEmail;
    private Button button;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("Account","firebaseAuth.getCurrentUser: " + firebaseAuth.getCurrentUser());

                if (firebaseAuth.getCurrentUser() == null){
                    removeOldUsersTokenFromDatabase(userIdOfLastUser);
                    MainActivity.setAllGamesIsCurrentView(true);
                } else {
                    userIdOfLastUser = firebaseAuth.getCurrentUser().getUid();
                }
            }
        });
        FirebaseUser user = auth.getCurrentUser();
        userIdOfLastUser = user.getUid();
        if (user == null){
            startActivity(new Intent(AccountActivity.this,SignUpActivity.class));
        }

        textViewTop = findViewById(R.id.textViewAccount);
        textViewEmail = findViewById(R.id.textview_account_email);
        textViewEmail.setText(user.getEmail());
        buttonBack = findViewById(R.id.account_button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button = findViewById(R.id.account_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                button.setEnabled(false);
                textViewTop.setVisibility(View.INVISIBLE);
                textViewEmail.setText("Du bist jetzt abgemeldet.");
            }
        });


    }
    private void removeOldUsersTokenFromDatabase(String oldUserId){
        final DatabaseReference firebaseUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseUsersRef.child(oldUserId).removeValue();
    }
}
