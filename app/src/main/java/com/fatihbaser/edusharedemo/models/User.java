package com.fatihbaser.edusharedemo.models;

public class User {
    //TODO: imageProfile null geliyor firebase. Bunu kullanmiyoruz hicbir yerde
    private String id;
    private String email;
    private String username;
    private String university;
    private String department;
    private String bio;
    private String image;
    private String imageProfile;
    private long timestamp;

    public User() { }

    public User(String id, String email, String username, String university, String department, String bio, String imageProfile,String image,long timestamp) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.university = university;
        this.department = department;
        this.bio = bio;
        this.imageProfile = imageProfile;
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
