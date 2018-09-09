package de.ur.mi.android.sportsfreund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//cf. https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/ for both concept and code bits.
public class SignUpActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SignUpActivity";

    private FirebaseAuth auth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_mail);
        editTextPassword = (findViewById(R.id.editText_password));
        Button buttonRegister = findViewById(R.id.registration_button_main);
        Button buttonForgotPassword = findViewById(R.id.registration_button_beneath_main);
        Button buttonSwitchToLogin = findViewById(R.id.registration_button_bottom);
        progressBar = findViewById(R.id.register_progressbar);

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,ResetPasswordActivity.class));
            }
        });
        buttonSwitchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),R.string.enter_email,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),R.string.enter_password,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6){
                    Toast.makeText(getApplicationContext(), R.string.password_too_short,Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email,password).
                        addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(LOG_TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()){
                                    Log.d(LOG_TAG,"exception: " + task.getException());
                                    Toast.makeText(SignUpActivity.this,
                                            getString(R.string.sign_out_failed),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this,getString(R.string.signUp_successful),Toast.LENGTH_SHORT).show();
                                    MyFirebaseMessagingService.sendTokenToDatabase(auth.getCurrentUser());
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        if (auth.getCurrentUser() != null){
            finish();
        }
    }

}


