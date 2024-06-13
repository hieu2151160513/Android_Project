package com.example.notesapp.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.databinding.ActivityMainScheduleBinding;
import com.example.notesapp.model.Schedule;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class MainScheduleActivity extends AppCompatActivity {

    ActivityMainScheduleBinding binding;
    ScheduleAdapter scheduleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTitle = s.toString();
                searchFireStore(searchTitle);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScheduleActivity.this, ScheduleDetailsActivity.class));
            }
        });

        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });

        binding.sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu();
            }
        });

        setUpRecyclerView();
    }

    void setUpRecyclerView() {
        Query query = Utility.getCollectionReferenceForSchedules().orderBy("createTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class).build();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(options, this);
        binding.recyclerView.setAdapter(scheduleAdapter);
    }

    void searchFireStore(String text) {
        Query query = Utility.getCollectionReferenceForSchedules().orderBy("title", Query.Direction.DESCENDING).startAt(text + "~");
        FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class).build();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter.updateOptions(options);
        binding.recyclerView.setAdapter(scheduleAdapter);
    }

    void showMenu() {
        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(MainScheduleActivity.this, binding.menuBtn);
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
                    startActivity(new Intent(MainScheduleActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Note List") {
                    startActivity(new Intent(MainScheduleActivity.this, SplashActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "ToDo List") {
                    startActivity(new Intent(MainScheduleActivity.this, SplashToDoListActivity.class));
                    finish();
                    return true;
                }
                if (item.getTitle() == "Profile") {
                    startActivity(new Intent(MainScheduleActivity.this, ProfileActivity.class));

                    return true;
                }
                if (item.getTitle() == "Schedules") {
                    startActivity(new Intent(MainScheduleActivity.this, SplashScheduleActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void showOptionMenu() {
        //TODO Display menu
        PopupMenu popupMenu = new PopupMenu(MainScheduleActivity.this, binding.sortBtn);
        popupMenu.getMenu().add("Sort title ascending");
        popupMenu.getMenu().add("Sort title descending");
        popupMenu.getMenu().add("Sort date ascending");
        popupMenu.getMenu().add("Sort date descending");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == "Sort title ascending") {
                    Query query = Utility.getCollectionReferenceForSchedules().orderBy("title", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                            .setQuery(query, Schedule.class).build();
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainScheduleActivity.this));
                    scheduleAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort title descending") {
                    Query query = Utility.getCollectionReferenceForSchedules().orderBy("title", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                            .setQuery(query, Schedule.class).build();
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainScheduleActivity.this));
                    scheduleAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort date ascending") {
                    Query query = Utility.getCollectionReferenceForSchedules().orderBy("createTime", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                            .setQuery(query, Schedule.class).build();
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainScheduleActivity.this));
                    scheduleAdapter.updateOptions(options);
                    return true;
                }
                if (item.getTitle() == "Sort date descending") {
                    Query query = Utility.getCollectionReferenceForSchedules().orderBy("createTime", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                            .setQuery(query, Schedule.class).build();
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainScheduleActivity.this));
                    scheduleAdapter.updateOptions(options);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        scheduleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scheduleAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestart() {
        super.onRestart();
        scheduleAdapter.notifyDataSetChanged();
    }

    public static class ScheduleAdapter extends FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder> {
        Context context;

        public ScheduleAdapter(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
            super(options);
            this.context = context;
        }

        @Override
        protected void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position, @NonNull Schedule schedule) {
            holder.title.setText(schedule.getTitle());
            holder.content.setText(schedule.getContent());
            holder.notifyIcon.setImageResource(R.drawable.baseline_notifications_active_24);
            holder.notifyDate.setText(schedule.getNotifyDate());
            holder.notifyTime.setText(schedule.getNotifyTime());
            holder.createTime.setText(Utility.timestampToString(schedule.getCreateTime()));
            if (!schedule.getImageUrl().equals("")) {
                Picasso.get().load(schedule.getImageUrl()).into(holder.imagePaint);
            }


            holder.itemView.setOnClickListener((v) -> {
                Intent intent = new Intent(context, ScheduleDetailsActivity.class);
                intent.putExtra("title", schedule.getTitle());
                intent.putExtra("content", schedule.getContent());
                intent.putExtra("date", schedule.getNotifyDate());
                intent.putExtra("time", schedule.getNotifyTime());
                intent.putExtra("image", schedule.getImageUrl());
                String docId = this.getSnapshots().getSnapshot(position).getId();

                intent.putExtra("docId", docId);
                context.startActivity(intent);
            });
        }

        @NonNull
        @Override
        public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
            return new ScheduleViewHolder(view);
        }

        static class ScheduleViewHolder extends RecyclerView.ViewHolder {
            TextView title, content, createTime, notifyDate, notifyTime;
            ImageView notifyIcon, imagePaint;

            public ScheduleViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.txt_schedule_title);
                content = itemView.findViewById(R.id.txt_schedule_content);
                createTime = itemView.findViewById(R.id.txt_schedule_timestamp);
                notifyDate = itemView.findViewById(R.id.txt_schedule_date);
                notifyTime = itemView.findViewById(R.id.txt_schedule_time);
                notifyIcon = itemView.findViewById(R.id.schedule_notifyicon);
                imagePaint = itemView.findViewById(R.id.img_schedule_from_draw);
            }
        }

    }
}