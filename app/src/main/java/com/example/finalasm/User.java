package com.example.finalasm;


import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private String userEmail ="", userName = "";
    private List<String> collection, mealCreate;
    private boolean isAdmin;
    private URL urlProfile;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userEmail, String userName, List<String> collection, List<String> mealCreate, URL urlProfile, boolean isAdmin) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.collection = collection;
        this.mealCreate = mealCreate;
        this.urlProfile = urlProfile;
        this.isAdmin = isAdmin;
    }

    // Update specific fields
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userEmail", userEmail);
        result.put("userName", userName);
        result.put("collection", collection);
        result.put("mealCreate", mealCreate);
        result.put("urlProfile", urlProfile);
        result.put("isAdmin", isAdmin);

        return result;
    }

    public URL getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(URL urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public List<String> getMealCreate() {
        return mealCreate;
    }

    public void setMealCreate(List<String> mealCreate) {
        this.mealCreate = mealCreate;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", collection=" + collection +
                ", mealCreate=" + mealCreate +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
