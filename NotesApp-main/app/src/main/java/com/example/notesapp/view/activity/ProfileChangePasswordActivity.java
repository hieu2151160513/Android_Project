package com.example.notesapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurrent, editTextPwdNew, editTextPwdConfirmNew;
    private Button btnChangePwd, btnReAuthentiacte;
    private String userPwdCurrent;
    private ProgressBar progressBar;

    private TextView textViewChangePwdAuthenticated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_password);

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurrent = findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew = findViewById(R.id.editText_confirm_change_pwd_new);
        btnChangePwd = findViewById(R.id.btn_change_pwd);
        btnReAuthentiacte = findViewById(R.id.btn_change_pwd_authenticate);
        textViewChangePwdAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.ProfileprogressBar);

        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        btnChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ProfileChangePasswordActivity.this, "Something went wrong ! User's details not available",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileChangePasswordActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthentiacteUser(firebaseUser);
        }
    }

    private void reAuthentiacteUser(FirebaseUser firebaseUser) {
        btnReAuthentiacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurrent = editTextPwdCurrent.getText().toString();

                if (TextUtils.isEmpty(userPwdCurrent)) {
                    Toast.makeText(ProfileChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextPwdCurrent.setError("Please enter your current password to authenticate");
                    editTextPwdCurrent.requestFocus();
                } else {
//                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurrent);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                editTextPwdCurrent.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);

                                btnReAuthentiacte.setEnabled(false);
                                btnChangePwd.setEnabled(true);

                                textViewChangePwdAuthenticated.setText("You are authenticated."
                                        + "You can change password now !");
                                Toast.makeText(ProfileChangePasswordActivity.this, "Password has been verified"
                                        + "Change password now", Toast.LENGTH_SHORT).show();
                                btnChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ProfileChangePasswordActivity.this, R.color.black));
                                btnChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ProfileChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ProfileChangePasswordActivity.this, "New Password is need", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(ProfileChangePasswordActivity.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter your new password");
            editTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ProfileChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please re-enter same password");
            editTextPwdConfirmNew.requestFocus();
        } else if (userPwdCurrent.matches(userPwdNew)) {
            Toast.makeText(ProfileChangePasswordActivity.this, "New Password cannot be same as old password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please enter a new password");
            editTextPwdConfirmNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileChangePasswordActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ProfileChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }
}