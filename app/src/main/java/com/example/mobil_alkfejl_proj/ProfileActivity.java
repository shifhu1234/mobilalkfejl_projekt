package com.example.mobil_alkfejl_proj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 987;
    private final int tag = 1;
    ImageView image;

    TextView accountUserName, accountEmail, accountPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        image = findViewById(R.id.imageView);

        accountUserName = findViewById(R.id.accountUserName);
        accountEmail = findViewById(R.id.accountEmail);
        accountPoints = findViewById(R.id.accountPoints);

//        accountUserName.setText(String.valueOf(user.getDisplayName()));
//        accountEmail.setText(String.valueOf(user.getEmail()));
        if (user != null) {
            accountEmail.setText("Email címed: " + user.getEmail());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("name");
                            long points = document.getLong("points");
                            if (name != null) {
                                accountUserName.setText("Neved: " + name);
                                accountPoints.setText("Pontjaid száma: " + points);
                            } else {
                                accountUserName.setText("ANONYMOUS");
                                accountPoints.setText(String.valueOf(0));
                            }
                        }
                    })
                    .addOnFailureListener(failure -> {
                        accountUserName.setText("HIBA");
                    });
        }
    }

    public void openCamera(View view) {
        checkUserPermission();
    }

    void checkUserPermission() {
//        takePicture();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, tag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, "Jogosultság megtiltva!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == tag && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Bitmap img = (Bitmap) b.get("data");
            image.setImageBitmap(img);
        }
    }

    public void changeUsername(View view) {
        TextView usernameInput = findViewById(R.id.usernameInput);
        String newUsername = usernameInput.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Adj meg új felhasználónevet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newUsername.length() < 3) {
            Toast.makeText(this, "Az új felhasználónév legalább 3 karakter hosszú!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("Users")
                                .document(document.getId())
                                .update("name", newUsername)
                                .addOnSuccessListener(success -> {
                                    accountUserName.setText("Neved: " + newUsername);
                                    Toast.makeText(this, "Felhasználónév frissítve!", Toast.LENGTH_SHORT).show();
                                    usernameInput.setText("");
                                })
                                .addOnFailureListener(failure -> Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    public void changePassword(View view) {
        TextView passwordInput = findViewById(R.id.passwordInput);
        String newPassword = passwordInput.getText().toString().trim();
        TextView newPasswordConfirmInput = findViewById(R.id.passwordConfirmInput);
        String newPasswordConfirm = newPasswordConfirmInput.getText().toString().trim();


        if (newPassword.length() < 6) {
            Toast.makeText(this, "Az új jelszó legalább 6 karakter hosszú!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(newPasswordConfirm)) {
            Toast.makeText(this, "Az jelszavaknak egyezniük kell!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.updatePassword(newPassword)
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Jelszó frissítve!", Toast.LENGTH_SHORT).show();
                    passwordInput.setText("");
                    newPasswordConfirmInput.setText("");
                })
                .addOnFailureListener(failure -> Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show());
    }

    public void deleteAccount(View view) {
        TextView deleteConfirmInput = findViewById(R.id.deleteConfirmInput);
        String confirm = deleteConfirmInput.getText().toString().trim();

        if (!confirm.equalsIgnoreCase(user.getEmail())) {
            Toast.makeText(this, "A saját emailed add meg a törléshez!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getUid().equals(user.getUid())) {
            Toast.makeText(this, "Csakis a saját fiókod törölheted! ;)", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("Users")
                                .document(document.getId())
                                .delete()
                                .addOnSuccessListener(success -> {
                                    user.delete()
                                            .addOnSuccessListener(success2 -> {
                                                Toast.makeText(this, "Felhasználó törölve! :(", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(failure -> Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(failure -> Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(failure -> Toast.makeText(this, "HIBA", Toast.LENGTH_SHORT).show());
    }

    public void backToCategories(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_row_3, R.anim.slide_in_row_4);
    }
}