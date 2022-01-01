package com.example.finalasm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth firebaseAuth;
    private EditText regEmail, regPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        regEmail = findViewById(R.id.userEmail);
        regPassword = findViewById(R.id.userPassword);
        Button signUp = findViewById(R.id.sign_upBtn);

        signUp.setOnClickListener(v -> {
            String inputEmail = regEmail.getText().toString();
            String inputPassword = regPassword.getText().toString();
            createAccount(inputEmail, inputPassword, isReg -> {
                if (isReg) {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("email", inputEmail);
                    Log.d(TAG, "input email on sent: " + inputEmail);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.w(TAG, "Error with callback");
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    private void createAccount(String email, String password, authCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    boolean isReg;
                    if (task.isSuccessful()) {
                        // If sign up success
                        Log.d(TAG, "Registration successfully.");
                        isReg = true;
                    } else {
                        // If sign up failed
                        Log.w(TAG, "Registration failed.", task.getException());
                        Toast.makeText(SignupActivity.this, "Sign up failed", Toast.LENGTH_LONG).show();
                        isReg = false;
                    }
                    callback.run(isReg);
                });
    }

    private interface authCallback {
        void run(boolean isReg);
    }
}