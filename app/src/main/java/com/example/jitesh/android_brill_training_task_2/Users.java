package com.example.jitesh.android_brill_training_task_2;

public class Users
{

    private String ThumbImage;
    private String Name, Email,Gender,DOB,Status;

    public Users()
    {
    }

    public Users(String thumbimage,String name, String email, String gender, String DOB,String status)
    {
        ThumbImage=thumbimage;
        Name = name;
        Email = email;
        Gender = gender;
        this.DOB = DOB;
        Status=status;
    }


    public String getThumbImage() {
        return ThumbImage;
    }

    public void setThumbImage(String thumbImage) {
        ThumbImage = thumbImage;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }


    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getGender() {
        return Gender;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDOB() {
        return DOB;
    }
}
