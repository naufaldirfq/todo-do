package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String name;
    public String image;
    @SuppressWarnings("WeakerAccess")
    public String email;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    public boolean isNew, isCreated;

    public User() {}

    public User(String uid, String name, String image, String email) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.email = email;
    }
}