package com.example.mobil_alkfejl_proj;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CategoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = CategoryActivity.class.getName();
    private FirebaseUser user;

    ConstraintLayout fruitCatergory;
    private AlarmManager mAlarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Azonositott felhasznalo");
        } else {
            Log.d(LOG_TAG, "Nem sikerult bejelentkeztetni a felhasznalot");
            finish();
        }

        fruitCatergory = findViewById(R.id.fruitCategory);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setmAlarmManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1234);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítések engedélyezve!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Értesítések letiltva! Engedélyezd a telefonod beállításaiban!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setmAlarmManager() {
//        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long repeatInterval = 60 * 1000;
        long triggertime = SystemClock.elapsedRealtime() + repeatInterval;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mAlarmManager.canScheduleExactAlarms();
//        }
        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggertime,
                repeatInterval,
                pendingIntent
        );

    }


    public void navigateToFruitProducts(View view) {
        Intent intent = new Intent(this, FruitProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToVegetableProducts(View view) {
        Intent intent = new Intent(this, VegetableProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToHoneyProducts(View view) {
        Intent intent = new Intent(this, HoneyProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }

    public void navigateToPastaProducts(View view) {
        Intent intent = new Intent(this, PastaProductActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row, R.anim.slide_in_row_2);
    }
}