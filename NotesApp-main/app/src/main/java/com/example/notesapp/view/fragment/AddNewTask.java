package com.example.notesapp.view.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notesapp.OnDialogCloseListener;
import com.example.notesapp.R;
import com.example.notesapp.Utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView setDueDate;
    private EditText taskEdit;
    private Button saveBtn;
    private Context context;
    private String dueDate = "";
    private String task;
    private String id = "";
    private String dueDateUpdate = "";

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    // b1
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);
    }

    // b2
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_tv);
        taskEdit = view.findViewById(R.id.task_editText);
        saveBtn = view.findViewById(R.id.save_btn);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("due");

            taskEdit.setText(task);
            setDueDate.setText(dueDateUpdate);

            if (task.length() > 0) {
                saveBtn.setEnabled(false);
                saveBtn.setBackgroundColor(Color.GRAY);
            }
        }


        taskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    saveBtn.setEnabled(false);
                    saveBtn.setBackgroundColor(Color.GRAY);
                } else {
                    saveBtn.setEnabled(true);
                    saveBtn.setBackgroundColor(getResources().getColor(R.color.green_blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalenderPicker();
            }
        });

        boolean finalIsUpdate = isUpdate;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String task = taskEdit.getText().toString();

                if (finalIsUpdate) {
                    Utility.getCollectionReferenceForToDoLists().document(id).update("task", task, "due", dueDate);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.isEmpty()) {
                        Toast.makeText(context, "Empty task not Allowed !!", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp()); // de sort by time

                        Utility.getCollectionReferenceForToDoLists().add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Task saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }

    void openCalenderPicker() {
        Calendar calendar = Calendar.getInstance();

        int MONTH = calendar.get(Calendar.MONTH);
        int YEAR = calendar.get(Calendar.YEAR);
        int DAY = calendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDueDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                dueDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        }, YEAR, MONTH, DAY);

        dialog.show();
    }

    // hàm này thực hiện tạo tham chiếu từ một fragment đến activity đã khởi tạo nó, và thực hiện một số bước trong quá trình khởi tạo
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
