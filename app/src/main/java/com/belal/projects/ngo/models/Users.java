package com.belal.projects.ngo.models;

public class Users {
    // make sure it matches the name of childs in database or it wont work
    public String first_name ;
    public String last_name ;
    public String status ;
    public String profile_image ;
    public String thumb_image ;

    // empty constructor without this the app may crash
    public Users(){}

    // now we need to get getter and setters ...

    public Users(String first_name, String last_name, String status, String profile_image, String thumb_image) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.status = status;
        this.profile_image = profile_image;
        this.thumb_image = thumb_image;
    }

    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getStatus() { return status; }
    public String getProfile_image() { return profile_image; }
    public String getThumb_image() { return thumb_image; }



    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public void setStatus(String status) { this.status = status; }
    public void setProfile_image(String profile_image) { this.profile_image = profile_image; }
    public void setThumb_image(String thumb_image) { this.thumb_image = thumb_image; }


} // Users
