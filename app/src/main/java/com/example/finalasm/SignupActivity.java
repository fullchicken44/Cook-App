package com.example.finalasm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth firebaseAuth;
    private EditText userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        Button signUp = findViewById(R.id.sign_upBtn);

        signUp.setOnClickListener(v -> {
            String inputEmail = userEmail.getText().toString();
            String inputPassword = userPassword.getText().toString();
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
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");
                                        }
                                    }
                                });

                        // If sign up success
                        Log.d(TAG, "Registration successfully.");
                        isReg = true;
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        final TextView alert = new EditText(this);
                        alert.setText("Please check your email for verification!");
                        alert.setTextSize(20);
                        builder.setView(alert);
                        builder.setTitle("Success");
                        builder.setMessage("")
                                .setNeutralButton("Dismiss", null)
                                .setIcon(android.R.drawable.ic_dialog_alert);
                        builder.show();
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

    ActionCodeSettings actionCodeSettings =
            ActionCodeSettings.newBuilder()
                    // URL you want to redirect back to. The domain (www.example.com) for this
                    // URL must be whitelisted in the Firebase Console.
                    .setUrl("https://www.finalandroid-e100e.web.app.com/finishSignUp?cartId=1234")
                    // This must be true
                    .setHandleCodeInApp(true)
                    .setIOSBundleId("com.example.ios")
                    .setAndroidPackageName(
                            "com.example.android",
                            true, /* installIfNotAvailable */
                            "12"    /* minimumVersion */)
                    .build();
}