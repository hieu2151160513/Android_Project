package com.example.notesapp.view.activity;

import org.jetbrains.annotations.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.notesapp.TouchHelper;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.adapter.ToDoAdapter;
import com.example.notesapp.model.Schedule;
import com.example.notesapp.model.ToDo;
import com.example.notesapp.view.fragment.AddNewTask;
import com.example.notesapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ToDoActivity extends AppCompatActivity {
    ImageButton menuBtn, sortmenuBtn;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private ToDoAdapter toDoAdapter;
    private List<ToDo> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        menuBtn = findViewById(R.id.menu_btn);
        sortmenuBtn = findViewById(R.id.sort_btn);
        recyclerView = findViewById(R.id.recycler_view);
        mFab = findViewById(R.id.add_note_btn);

        menuBtn.setOnClickListener((v) -> showMenu());
        sortmenuBtn.setOnClickListener((v) -> showSortMenu());


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // viet new task thi ta se goi method newInstance(), ho tro trinh quan ly phan doan
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

//        recyclerView.setLayoutManager(new LinearLayoutManager(ToDoActivity.this));
        mList = new ArrayList<>();
        toDoAdapter = new ToDoAdapter(ToDoActivity.this, mList);
//
        showData();
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//        recyclerView.setAdapter(toDoAdapter);
    }


    private void showData() {
        Utility.getCollectionReferenceForToDoLists().orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDo toDoModel = documentChange.getDocument().toObject(ToDo.class).withId(id);
                        mList.add(toDoModel);
                        toDoAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(toDoAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestart() {
        super.onRestart();
        toDoAdapter.notifyDataSetChanged();
    }

    void showMenu() {
        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(ToDoActivity.this, menuBtn);
        popupMenu.getMenu().add("Note List");
        popupMenu.getMenu().add("Schedule");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("ToDo List");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ToDoActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Note List") {
                    startActivity(new Intent(ToDoActivity.this, MainActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "ToDo List") {
                    startActivity(new Intent(ToDoActivity.this, SplashToDoListActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Profile") {
                    startActivity(new Intent(ToDoActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Schedule") {
                    startActivity(new Intent(ToDoActivity.this, SplashScheduleActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void showSortMenu() {
        //TODO Display Sort menu
        PopupMenu popupMenu = new PopupMenu(ToDoActivity.this, sortmenuBtn);
        popupMenu.getMenu().add("Sort A to Z");
        popupMenu.getMenu().add("Sort Z to A");
        popupMenu.getMenu().add("Sort date ascending");
        popupMenu.getMenu().add("Sort date descending");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Sort A to Z") {
                    mList.sort(ToDo.ToDoTaskAZComparator);
                    Toast.makeText(ToDoActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                    toDoAdapter = new ToDoAdapter(ToDoActivity.this, mList);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
                    itemTouchHelper.attachToRecyclerView(recyclerView);

                    recyclerView.setAdapter(toDoAdapter);
                }
                if (item.getTitle() == "Sort Z to A") {
                    mList.sort(ToDo.ToDoTaskZAComparator);
                    Toast.makeText(ToDoActivity.this, "Sort Z to A", Toast.LENGTH_SHORT).show();
                    toDoAdapter = new ToDoAdapter(ToDoActivity.this, mList);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
                    itemTouchHelper.attachToRecyclerView(recyclerView);

                    recyclerView.setAdapter(toDoAdapter);
                }
                return true;
            }
        });
    }
}