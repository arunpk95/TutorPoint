package com.example.tutorpoint.modals;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    String name, email, password;
    List<String> languages;
    String bio,Country;

    public User(String name, String email, String password, List<String> languages, String bio, String country) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.languages = languages;
        this.bio = bio;
        Country = country;
    }
    public User(){}
}
