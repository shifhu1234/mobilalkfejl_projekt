package com.example.mobil_alkfejl_proj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
//    private static final int SECRET_KEY = 986;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText userPasswordAgainEditText;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Bundle bundle = getIntent().getExtras();
//        int secret_key = bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 987) {
            finish();
        }

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userPasswordAgainEditText = findViewById(R.id.userPasswordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(userName);
        userPasswordEditText.setText(password);
        userPasswordAgainEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {

        String userName = userNameEditText.getText().toString();
        String userEmail = userEmailEditText.getText().toString();
        String userPassword = userPasswordEditText.getText().toString();
        String userPasswordAgian = userPasswordAgainEditText.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Minden mezőt szükséges kitölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Minden mezőt szükséges kitölteni!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
//            Log.e(LOG_TAG, "Nem egyező jelszavak!");
            Toast.makeText(this, "Minimum 6 karakter hosszú jelszó szükséges!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!userPassword.equals(userPasswordAgian)) {
//            Log.e(LOG_TAG, "Nem egyező jelszavak!");
            Toast.makeText(this, "Nem egyező jelszavak!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Nem létező email cím!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName.length() < 3) {
            Toast.makeText(this, "Minimum 3 karakter hosszú felhasználónév szükséges!", Toast.LENGTH_SHORT).show();
            return;
        }

//        startShopping();
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Felhasznalo letrehozva");
                    startShopping();
                } else {
                    Log.d(LOG_TAG, "Sikertelen felhasználó létrehozás", task.getException());
                    if (task.getException() != null) {
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage != null && errorMessage.contains("email address is already in use")) {
                            Toast.makeText(RegisterActivity.this, "Ez az e-mail cím már használatban van!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Hiba! " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    public void cancel(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
    }

    private void startShopping() {
        Intent intent = new Intent(this, CategoryActivity.class);
//        intent.putExtra("SECTER_KEY", SECRET_KEY);
        startActivity(intent);
        Toast.makeText(this, "Üdvözlünk!", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}