package com.example.notesapp.model;

import java.util.Comparator;

public class ToDo extends TaskId {
    private String task;
    private String due;
    private  int status;

    public static Comparator<ToDo> ToDoTaskAZComparator = new Comparator<ToDo>() {

        @Override
        public int compare(ToDo o1, ToDo o2) {
            return o1.getTask().compareTo(o2.getTask());
        }
    };

    public static Comparator<ToDo> ToDoTaskZAComparator = new Comparator<ToDo>() {
        @Override
        public int compare(ToDo o1, ToDo o2) {
            return o2.getTask().compareTo(o1.getTask());
        }
    };

    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
