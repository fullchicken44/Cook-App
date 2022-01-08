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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth firebaseAuth;
    private EditText userFirstName, userLastName, userEmail, userPassword;
    DatabaseReference mealDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("meal");
    DatabaseReference userDb = FirebaseDatabase.getInstance
            ("https://finalandroid-e100e-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("user").child("users");
    FirebaseDB provider = new FirebaseDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        userFirstName = findViewById(R.id.userFirstName);
        userLastName = findViewById(R.id.userLastName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        Button signUp = findViewById(R.id.sign_upBtn);

        signUp.setOnClickListener(v -> {
            String inputEmail = userEmail.getText().toString();
            String inputPassword = userPassword.getText().toString();
            String inputName = userFirstName.getText().toString() + " " + userLastName.getText().toString();
            createAccount(inputName, inputEmail, inputPassword, isReg -> {
                if (isReg) {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("email", inputEmail);
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

    private void createAccount(String name, String email, String password, authCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    boolean isReg;
                    if (task.isSuccessful()) {
                        User user = new User(email, name, new ArrayList<>(), new ArrayList<>(), false);
                        // If sign up success
                        String key = userDb.push().getKey();
                        assert key != null;
                        userDb.child(key).setValue(user).addOnSuccessListener(unused -> Log.d("REGISTER: ","SUCCESS"));
                        Log.d("Registration successfully.", user.toString());
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