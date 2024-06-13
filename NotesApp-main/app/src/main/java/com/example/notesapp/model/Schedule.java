package com.example.notesapp.model;

import com.google.firebase.Timestamp;

public class Schedule {

    private String content;
    private String title;
    private Timestamp createTime;
    private String notifyDate, notifyTime;
    //List<Byte> imagePaint;
    private String imageUrl;
    public Schedule() {

    }

    public Schedule(String content, String title, Timestamp createTime, String notifyDate, String notifyTime/*, List<Byte> imagePaint*/, String imageUrl) {
        this.content = content;
        this.title = title;
        this.createTime = createTime;
        this.notifyDate = notifyDate;
        this.notifyTime = notifyTime;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }
}
