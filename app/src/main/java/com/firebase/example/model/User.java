package com.firebase.example.model;

public class User {

    private String ID, FirstName, LastName, Email, Mobile, ProfilePic, UserName, UID;

    public User() {
    }

    public User(String ID, String firstName, String lastName, String email, String mobile, String profilePic, String userName, String UID) {
        this.ID = ID;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Email = email;
        this.Mobile = mobile;
        this.ProfilePic = profilePic;
        this.UserName = userName;
        this.UID = UID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}

