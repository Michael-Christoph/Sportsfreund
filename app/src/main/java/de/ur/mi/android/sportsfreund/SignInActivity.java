package de.ur.mi.android.sportsfreund;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//cf. https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/ for both concept and code bits.
public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView textViewTop;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonResetPassword;
    private Button buttonSwitchToSignUp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        //users is already logged in
        if (auth.getCurrentUser() != null){
            startActivity(new Intent(SignInActivity.this,MainActivity.class));
        }

        //MP: View will be modified in the following lines.
        setContentView(R.layout.activity_sign_up);
        textViewTop = findViewById(R.id.textview_top);
        textViewTop.setText(R.string.login_request);
        editTextEmail = (EditText) findViewById(R.id.editText_mail);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        buttonLogin = (Button) findViewById(R.id.registration_button_main);
        buttonLogin.setText(R.string.button_login_text);
        buttonResetPassword = findViewById(R.id.registration_button_beneath_main);
        buttonSwitchToSignUp = findViewById(R.id.registration_button_bottom);
        buttonSwitchToSignUp.setText(R.string.button_link_to_signUp_text);
        progressBar = findViewById(R.id.register_progressbar);


        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,ResetPasswordActivity.class));
            }
        });
        buttonSwitchToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInOrHandleErrors();
            }
        });


    }
    private void signInOrHandleErrors(){
        String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                editTextPassword.setError(getString(R.string.password_too_short));
                            } else {
                                Toast.makeText(SignInActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //if (!MyFirebaseMessagingService.mostRecentTokenSavedInDatabase){
                                MyFirebaseMessagingService.sendTokenToDatabase(auth.getCurrentUser());
                            //}
                            finish();
                        }
                    }
                });
    }
}
