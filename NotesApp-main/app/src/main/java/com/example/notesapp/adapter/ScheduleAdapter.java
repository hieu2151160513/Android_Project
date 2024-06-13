package com.example.notesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.model.Schedule;
import com.example.notesapp.view.activity.ScheduleDetailsActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ScheduleAdapter extends FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder> {
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
            Picasso.get().load(schedule.getImageUrl()).fit().into(holder.imagePaint);
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
