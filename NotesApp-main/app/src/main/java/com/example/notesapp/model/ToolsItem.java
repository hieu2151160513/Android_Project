package com.example.notesapp.model;

public class ToolsItem {
    int rscID;
    String name;

    public ToolsItem(){

    }

    public ToolsItem(int rscID, String name) {
        this.rscID = rscID;
        this.name = name;
    }

    public int getRscID() {
        return rscID;
    }

    public void setRscID(int rscID) {
        this.rscID = rscID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
