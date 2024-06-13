package com.example.notesapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.model.ProfileUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    EditText editTextname, editTextsex, editTextphone, editTextDate;
    TextView textViewemail;
    ImageView imageUser;
    Button btnProfileSave, btnChangePwd;
    ImageButton btnMenu;
    DatePickerDialog pickerDialog;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    boolean a = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editTextname = findViewById(R.id.profile_name);
        textViewemail = findViewById(R.id.profile_email);
        editTextDate = findViewById(R.id.profile_date);
        editTextsex = findViewById(R.id.profile_sex);
        editTextphone = findViewById(R.id.profile_phone);
        imageUser = findViewById(R.id.userImage);
        btnProfileSave = findViewById(R.id.btnProfileSave);
        btnChangePwd = findViewById(R.id.btnChangePwd);
        btnMenu = findViewById(R.id.btnmenu);

        editTextname.setEnabled(!a);
        editTextphone.setEnabled(!a);
        editTextsex.setEnabled(!a);
        editTextDate.setEnabled(!a);
        loadDataProfile();
        btnMenu.setOnClickListener((v) -> showMenu());

        btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a) {
                    openUploadData();
                } else {
                    saveDataProfile();
                    loadDataProfile();
                }
            }
        });


        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileImageUser.class);
                startActivity(intent);

            }
        });

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileChangePasswordActivity.class);
                startActivity(intent);

            }
        });


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                pickerDialog.show();
            }
        });
    }

    private void showMenu() {

        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, btnMenu);
        popupMenu.getMenu().add("Note List");
        popupMenu.getMenu().add("Schedule");
        popupMenu.getMenu().add("ToDo List");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Note List") {
                    startActivity(new Intent(ProfileActivity.this, SplashActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "ToDo List") {
                    startActivity(new Intent(ProfileActivity.this, SplashToDoListActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Profile") {
                    startActivity(new Intent(ProfileActivity.this, SplashProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

    }

    void saveDataProfile() {
        String nameText = editTextname.getText().toString();
        String dateText = editTextDate.getText().toString();
        String sexText = editTextsex.getText().toString();
        String phoneText = editTextphone.getText().toString();

        String phoneRegex = "[0-9][0-9]{9}";
        Matcher phoneMatcher;
        Pattern phonePattern = Pattern.compile(phoneRegex);
        phoneMatcher = phonePattern.matcher(phoneText);

        if (TextUtils.isEmpty(nameText)) {
            Toast.makeText(ProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            editTextname.setError("Full name is required");
            editTextname.requestFocus();
        } else if (TextUtils.isEmpty(dateText)) {
            Toast.makeText(ProfileActivity.this, "Please your date of birth", Toast.LENGTH_SHORT).show();
            editTextDate.setError("Date of birth is required");
            editTextDate.requestFocus();
        } else if (TextUtils.isEmpty(dateText)) {
            Toast.makeText(ProfileActivity.this, "Please your sex", Toast.LENGTH_SHORT).show();
            editTextsex.setError("Sex is required");
            editTextsex.requestFocus();
        } else if (TextUtils.isEmpty(dateText)) {
            Toast.makeText(ProfileActivity.this, "Please your mobiphone number", Toast.LENGTH_SHORT).show();
            editTextsex.setError("Mobiphone number is required");
            editTextsex.requestFocus();
        } else if (!phoneMatcher.find()) {
            editTextphone.setError("Mobile not valid");
        } else {
            closeUploadData();
            ProfileUser user = new ProfileUser(nameText, dateText, sexText, phoneText);
            saveProfileUserToFireBase(user);
        }
    }

    void saveProfileUserToFireBase(ProfileUser user) {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForProfileUser().document("User" + firebaseUser.getUid());
        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(ProfileActivity.this, "Updated profile was successfully");
                } else {
                    Utility.showToast(ProfileActivity.this, "Updated profile was unsuccessfully");
                }
            }
        });

    }

    void loadDataProfile() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.get().load(uri).into(imageUser);

        textViewemail.setText(firebaseUser.getEmail());
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForProfileUser().document("User" + firebaseUser.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                editTextDate.setText(value.getString("date"));
                editTextname.setText(value.getString("name"));
                editTextphone.setText(value.getString("phone"));
                editTextsex.setText(value.getString("sex"));
            }
        });


    }

    void openUploadData() {
        btnProfileSave.setBackgroundTintList(ContextCompat.getColorStateList(
                ProfileActivity.this, R.color.black));
        btnProfileSave.setText("Save");
        editTextname.setEnabled(a);
        editTextphone.setEnabled(a);
        editTextsex.setEnabled(a);
        editTextDate.setEnabled(a);
        a = false;
    }

    void closeUploadData() {
        btnProfileSave.setBackgroundTintList(ContextCompat.getColorStateList(
                ProfileActivity.this, R.color.teal_200));
        btnProfileSave.setText("Update Profile");
        editTextname.setEnabled(a);
        editTextphone.setEnabled(a);
        editTextsex.setEnabled(a);
        editTextDate.setEnabled(a);
        a = true;
    }
}