package com.example.notesapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static CollectionReference getCollectionReferenceForNotes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("notes")
                    .document(currentUser.getUid()).collection("my_notes");
        }
        return null;
    }

    public static CollectionReference getCollectionReferenceForToDoLists() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("todolist")
                    .document(currentUser.getUid()).collection("my_todolist");
        }
        return null;
    }

    public static CollectionReference getCollectionReferenceForProfileUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("ProfileUser");
        }
        return null;
    }

    public static  CollectionReference getCollectionReferenceForSchedules() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return FirebaseFirestore.getInstance().collection("schedules")
                    .document(currentUser.getUid()).collection("my_schedules");
        }
        return null;
    }

    public static String timestampToString(Timestamp timestamp) {
        @SuppressLint("SimpleDateFormat") String format = new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
        return format;
    }
}
