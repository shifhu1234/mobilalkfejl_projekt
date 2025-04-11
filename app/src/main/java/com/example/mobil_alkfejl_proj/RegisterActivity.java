package com.example.mobil_alkfejl_proj;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText userPasswordAgainEditText;

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
        if (secret_key != 987){
            finish();
        }

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userPasswordAgainEditText = findViewById(R.id.userPasswordAgainEditText);

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {

        String userName = userNameEditText.getText().toString();
        String userEmail = userEmailEditText.getText().toString();
        String userPassword = userPasswordEditText.getText().toString();
        String userPasswordAgian = userPasswordAgainEditText.getText().toString();

        if (!userPassword.equals(userPasswordAgian)){
            Log.e(LOG_TAG, "Nem egyező jelszavak!");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + userName + ", email: " + userEmail);
        //TODO: regisztráció
    }

    public void cancel(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
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