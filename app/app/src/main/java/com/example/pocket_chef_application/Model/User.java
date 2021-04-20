package com.example.pocket_chef_application.Model;

import androidx.room.Ignore;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class User {
    private String fullName, DOB, email, UID;
    private @ServerTimestamp Date Timestamp;
    public User(){
    }

    public User(String fullName, String DOB, String email, String UID){
        this.fullName = fullName;
        this.DOB = DOB;
        this.email = email;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDOB() {
        return DOB;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
