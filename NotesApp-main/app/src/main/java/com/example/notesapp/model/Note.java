package com.example.notesapp.model;

import com.google.firebase.Timestamp;

public class Note {
    private String content;
    private String title;
    private Timestamp timestamp;

    public Note() {
    }

    public Note(String content, String title, Timestamp timestamp) {
        this.content = content;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
