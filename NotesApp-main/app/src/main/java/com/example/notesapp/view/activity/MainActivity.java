package com.example.notesapp.view.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.model.Note;
import com.example.notesapp.adapter.NoteAdapter;
import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.model.Schedule;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn, sortMenuBtn;
    NoteAdapter noteAdapter;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);
        sortMenuBtn = findViewById(R.id.sort_btn);
        search = findViewById(R.id.search_field);

        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteDetailsActivity.class)));
        menuBtn.setOnClickListener((v) -> showMenu());
        sortMenuBtn.setOnClickListener((v) -> showSortMenu());
        notificationApp();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                searchFireStore(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setupRecyclerView();
    }

    void searchFireStore(String text) {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("title", Query.Direction.DESCENDING). startAt(text + "~");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter.updateOptions(options);
        recyclerView.setAdapter(noteAdapter);
    }

    void showMenu() {
        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
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
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Note List") {
                    startActivity(new Intent(MainActivity.this, SplashActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "ToDo List") {
                    startActivity(new Intent(MainActivity.this, SplashToDoListActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Profile") {
                    startActivity(new Intent(MainActivity.this, SplashProfileActivity.class));
                    return true;
                }
                if (item.getTitle() == "Schedule") {
                    startActivity(new Intent(MainActivity.this, SplashScheduleActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    void showSortMenu() {
        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, sortMenuBtn);
        popupMenu.getMenu().add("Sort title ascending");
        popupMenu.getMenu().add("Sort title descending");
        popupMenu.getMenu().add("Sort date ascending");
        popupMenu.getMenu().add("Sort date descending");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Sort title ascending") {
                    Query query = Utility.getCollectionReferenceForNotes().orderBy("title", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query, Note.class).build();
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    noteAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort title descending") {
                    Query query = Utility.getCollectionReferenceForNotes().orderBy("title", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query, Note.class).build();
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    noteAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort date ascending") {
                    Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query, Note.class).build();
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    noteAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort date descending") {
                    Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                            .setQuery(query, Note.class).build();
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    noteAdapter.updateOptions(options);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestart() {
        super.onRestart();
        noteAdapter.notifyDataSetChanged();
    }
    public void notificationApp(){
        Context context = getApplicationContext();

        Intent intent = new Intent(context, NoteDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Let save note !")
                .setContentText("Please save some notes!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Please save some notes!! Schedule your day!"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}