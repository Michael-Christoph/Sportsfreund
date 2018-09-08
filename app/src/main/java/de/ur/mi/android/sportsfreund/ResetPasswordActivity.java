package de.ur.mi.android.sportsfreund;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

//cf. https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/ for both concept and code bits.
public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextView textViewTop;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonReset;
    private Button buttonBack;
    private Button buttonInvisible;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        textViewTop = findViewById(R.id.textview_top);
        textViewTop.setText(R.string.explain_password_reset);
        editTextEmail = findViewById(R.id.editText_mail);
        editTextPassword = findViewById(R.id.editText_password);
        editTextPassword.setVisibility(View.INVISIBLE);
        buttonReset = findViewById(R.id.registration_button_main);
        buttonReset.setText(R.string.reset_password);
        buttonBack = findViewById(R.id.registration_button_beneath_main);
        buttonBack.setText(R.string.back);
        buttonInvisible = findViewById(R.id.registration_button_bottom);
        buttonInvisible.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.register_progressbar);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, R.string.send_mail_successful, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, R.string.send_mail_failed, Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

    }
}
