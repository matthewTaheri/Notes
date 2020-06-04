package com.example.a4;

import androidx.annotation.NonNull;

public class DataClass {
    String noteName = "";
    String note = "";
    Long id ;
    String date = "";

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    @NonNull
    @Override
    public String toString() {
        return noteName + "\n" + note;
    }
}
