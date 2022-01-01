package com.example.finalasm;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    TextView sign_up, asGuest;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
            if (resultCode == RESULT_OK) {
                assert data != null;
                String receivedEmail = data.getStringExtra("email");
                email.setText(receivedEmail);
                Toast.makeText(this, "Sign up successful. Please log in.", Toast.LENGTH_LONG).show();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.userPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        sign_up = (TextView) findViewById(R.id.sign_up);
        asGuest = (TextView) findViewById(R.id.asGuest);

        loginBtn.setOnClickListener(v -> {
            String inputEmail = email.getText().toString();
            String inputPassword = password.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            user.reload();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        sign_up.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, SignupActivity.class);
            startActivity(intent1);
        });

        asGuest.setOnClickListener(v -> finish());

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
}
