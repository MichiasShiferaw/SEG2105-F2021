package com.example.deliverable1fixed;

public class User {

    public String fullName;
    public String age;
    public String email;
    public String username;
    public String type;
    public String password;

    public User () {}

    public User(String fullName, String age, String email, String username, String type, String password){
        this.fullName = fullName;
        this.age=age;
        this.email=email;
        this.username=username;
        this.type=type;
        this.password=password;
    }
}