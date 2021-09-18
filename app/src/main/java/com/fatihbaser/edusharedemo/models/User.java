package com.fatihbaser.edusharedemo.models;

public class User {


    private String id;
    private String email;
    private String username;

    private String university;
    private String department;
    private String bio;




    public User() {



        }
        public User(String id, String email, String username, String university, String department, String bio) {
        this.id = id;
        this.email = email;
        this.username = username;

        this.university = university;
        this.department = department;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
