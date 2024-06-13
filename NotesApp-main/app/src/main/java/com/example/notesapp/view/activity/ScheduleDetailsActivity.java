package com.example.notesapp.view.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notesapp.AlarmReceiver;
import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.databinding.ActivityScheduleDetailsBinding;
import com.example.notesapp.model.Schedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;


public class ScheduleDetailsActivity extends AppCompatActivity {

    ActivityScheduleDetailsBinding binding;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    LocalDateTime currentTime;
    boolean isEditMode = false;
    String title, content, date, time, docId, image;
    byte[] bytes;
    Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_schedule), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intent = new Intent(ScheduleDetailsActivity.this, MainPaintActivity.class);

        calendar = Calendar.getInstance();
        binding = ActivityScheduleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        image = getIntent().getStringExtra("image");
        docId = getIntent().getStringExtra("docId");

        bytes = getIntent().getByteArrayExtra("imagePaint");

        editor.putString("docId", docId);
        editor.apply();

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        binding.scheduleTitleText.setText(title);
        binding.scheduleContentText.setText(content);
        binding.txtScheduleDateAdd.setText(date);
        binding.txtScheduleTimeAdd.setText(time);
        if (image != null && !image.equals("")) {
            Picasso.get().load(image).fit().into(binding.schdeuleImagePaint);
            binding.cardView.setVisibility(View.VISIBLE);
            binding.schdeuleImagePaint.setVisibility(View.VISIBLE);
            binding.btnScheduleDeleteImagePaint.setVisibility(View.VISIBLE);
        }

        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.schdeuleImagePaint.setScaleType(ImageView.ScaleType.FIT_CENTER);
            binding.schdeuleImagePaint.setImageBitmap(bitmap);
            binding.cardView.setVisibility(View.VISIBLE);
            binding.schdeuleImagePaint.setVisibility(View.VISIBLE);
            binding.btnScheduleDeleteImagePaint.setVisibility(View.VISIBLE);
        }

        if (isEditMode) {
            binding.schedulePageTitle.setText("Edit your schedule");
            binding.deleteScheduleTextViewBtn.setVisibility(View.VISIBLE);
        }

        binding.btnScheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        binding.btnScheduleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.btnScheduleDrawIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.schdeuleImagePaint.getDrawable();
                //intent.putExtra("paintView", bitmapDrawable);
                startActivity(intent);
                finish();
            }
        });

        binding.saveScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });

        binding.deleteScheduleTextViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteScheduleFromFirebase();
                deleteAlarm();
            }
        });

        binding.btnScheduleDeleteImagePaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImagePaintFromFirebase();
            }
        });

    }

    void saveSchedule() {
        String scheduleTitle = binding.scheduleTitleText.getText().toString();
        String scheduleContent = binding.scheduleContentText.getText().toString();
        String scheduleDate = binding.txtScheduleDateAdd.getText().toString();
        String scheduleTime = binding.txtScheduleTimeAdd.getText().toString();
        if (scheduleTitle.isEmpty()) {
            binding.scheduleTitleText.setError("Title is required");
            return;
        }
        Schedule schedule = new Schedule(scheduleContent, scheduleTitle, Timestamp.now(), scheduleDate, scheduleTime, "");
        setAlarm(schedule);
        if (bytes != null) {
            saveImagePaintToStorage(schedule);
        } else {
            saveScheduleToFirebase(schedule);
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    void setAlarm(Schedule schedule) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        editor.putString("notifyTitle", schedule.getTitle());
        editor.putString("notifyContent", schedule.getContent());
        editor.apply();
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Utility.showToast(ScheduleDetailsActivity.this, "Set notification successfully");
    }

    void saveImagePaintToStorage(Schedule schedule) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ScheduleImages");
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageReference = storageReference.child(fileName);

        UploadTask uploadTask = imageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        schedule.setImageUrl(imageUrl);
                        image = imageUrl;
                        saveScheduleToFirebase(schedule);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    void saveScheduleToFirebase(Schedule schedule) {
        DocumentReference documentReference;

        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForSchedules().document(docId);
        } else {
            documentReference = Utility.getCollectionReferenceForSchedules().document();
        }

        if (bytes == null && binding.cardView.getVisibility() == View.VISIBLE) {
            String scheduleTitle = binding.scheduleTitleText.getText().toString();
            String scheduleContent = binding.scheduleContentText.getText().toString();
            String scheduleDate = binding.txtScheduleDateAdd.getText().toString();
            String scheduleTime = binding.txtScheduleTimeAdd.getText().toString();

            documentReference.update("title", scheduleTitle);
            documentReference.update("content", scheduleContent);
            documentReference.update("notifyDate", scheduleDate);
            documentReference.update("notifyTime", scheduleTime);
            documentReference.update("createTime", Timestamp.now());
            Utility.showToast(ScheduleDetailsActivity.this, "Schedule update successfully");
            finish();
        } else {
            documentReference.set(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Utility.showToast(ScheduleDetailsActivity.this, "Schedule add successfully");
                        finish();
                    } else {
                        Utility.showToast(ScheduleDetailsActivity.this, "Schedule add unsuccessfully");
                    }
                }
            });
        }
    }

    void deleteAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
    }

    void deleteImagePaintFromFirebase() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForSchedules().document(docId);
//        ImageView imageView = findViewById(R.id.img_schedule_from_draw);
//        imageView.setVisibility(View.GONE);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                binding.schdeuleImagePaint.setVisibility(View.GONE);
                binding.btnScheduleDeleteImagePaint.setVisibility(View.GONE);
                binding.cardView.setVisibility(View.GONE);
                documentReference.update("imageUrl", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    void deleteScheduleFromFirebase() {
        if (binding.schdeuleImagePaint.getVisibility() == View.VISIBLE) {
            deleteImagePaintFromFirebase();
        }
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForSchedules().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(ScheduleDetailsActivity.this, "Schedule delete successfully");
                    finish();
                } else {
                    Utility.showToast(ScheduleDetailsActivity.this, "Failed while deleting schedule");
                }
            }
        });
    }

    void showDatePicker() {
        currentTime = LocalDateTime.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.txtScheduleDateAdd.setText(dayOfMonth + " / " + (month+1) + " / " + year);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
            }
        }, currentTime.getYear(), currentTime.getMonthValue() - 1, currentTime.getDayOfMonth());

        datePickerDialog.show();
    }

    void showTimePicker() {
        currentTime = LocalDateTime.now();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                binding.txtScheduleTimeAdd.setText(hourOfDay + " : " + minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        }, currentTime.getHour(), currentTime.getMinute(), true);

        timePickerDialog.show();
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "noteAndroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("noteAndroid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}