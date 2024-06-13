package com.example.notesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.example.notesapp.model.ToDo;
import com.example.notesapp.view.activity.ToDoActivity;
import com.example.notesapp.view.fragment.AddNewTask;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDo> toDoList;
    private ToDoActivity toDoActivity;
    Context context;

    public ToDoAdapter(ToDoActivity toDoActivity, List<ToDo> toDoList) {
        this.toDoList = toDoList;
        this.toDoActivity = toDoActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_todo_item, parent, false);
        return new MyViewHolder(view);
    }

    public void deleteTask(int position) {
        ToDo toDoModel = toDoList.get(position);
        Utility.getCollectionReferenceForToDoLists().document(toDoModel.TaskId).delete();
        toDoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext() {
        return toDoActivity;
    }

    public void editTask(int position) {
        ToDo toDoModel = toDoList.get(position);
//        Intent intent = new Intent(context, AddNewTask.class);
//        intent.putExtra("task", toDoModel.getTask());
//        intent.putExtra("due", toDoModel.getDue());
//        intent.putExtra("id", toDoModel.TaskId);
//
//        AddNewTask addNewTask = new AddNewTask();
//        context.startActivity(intent);

        Bundle bundle = new Bundle();
        bundle.putString("task", toDoModel.getTask());
        bundle.putString("due", toDoModel.getDue());
        bundle.putString("id", toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(toDoActivity.getSupportFragmentManager(), addNewTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDo toDoModel = toDoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());
        holder.mDueDateTv.setText("Due On " + toDoModel.getDue());
        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        // Listen on change
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Task<Void> documentReference;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utility.getCollectionReferenceForToDoLists().document(toDoModel.TaskId).update("status", 1);
                } else {
                    Utility.getCollectionReferenceForToDoLists().document(toDoModel.TaskId).update("status", 0);
                }
            }
        });
    }

    private boolean toBoolean(int status) {
        return status != 0;
        // if status == 1 return true; else return false
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mDueDateTv;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}

//public class ToDoAdapter extends FirestoreRecyclerAdapter<ToDo, ToDoAdapter.ToDoViewHolder> {
//    /**
//     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
//     * FirestoreRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
//
//    Context context;
//    ToDoActivity toDoActivity;
//
//    // b4
//    public ToDoAdapter(@NonNull FirestoreRecyclerOptions<ToDo> options, Context context) {
//        super(options);
//        this.context = context;
//    }
//
//    // b3
//    @Override
//    protected void onBindViewHolder(@NonNull ToDoAdapter.ToDoViewHolder holder, int position, @NonNull ToDo toDoModel) {
//
//        holder.mCheckBox.setText(String.valueOf(toDoModel.getTask()));
//        holder.mDueDateTv.setText("Due on " + (toDoModel.getDue()));
//
//        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));
//
//        String toDoId = this.getSnapshots().getSnapshot(position).getId();
//        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            Task<Void> documentReference;
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    documentReference = Utility.getCollectionReferenceForToDoLists().document(toDoId).update("status", 1);
//                } else {
//                    documentReference = Utility.getCollectionReferenceForToDoLists().document(toDoId).update("status", 0);
//                }
//            }
//        });
//
////        holder.itemView.setOnClickListener((v) -> {
////            Intent intent - new Intent(context, )
////        });
//    }
//
//    private boolean toBoolean(int status) {
//        return status != 0;
//        // if status == 1 return true; else return false
//    }
//
//    // b2
//    @NonNull
//    @Override
//    public ToDoAdapter.ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_todo_item, parent, false);
//        return new ToDoViewHolder(view);
//    }
//
//    public Context getContext() {
//        return toDoActivity;
//    }
//
//
//    // b1
//    public class ToDoViewHolder extends RecyclerView.ViewHolder {
//        TextView mDueDateTv;
//        CheckBox mCheckBox;
//
//        public ToDoViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
//            mCheckBox = itemView.findViewById(R.id.mcheckbox);
//        }
//    }
//}
