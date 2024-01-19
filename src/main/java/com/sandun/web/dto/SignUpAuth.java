package com.sandun.web.dto;

import com.sandun.web.entities.UserType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SignUpAuth {
    @NotNull(message = "Please Enter Your Enter Your Email Or Contact No")
    private String fName;
    private String lName;
    @NotNull(message = "Please Enter Your Enter Your Email Or Contact No")
    @Pattern(regexp = "^(?:(\\+94|0)(\\d{9}|\\d{2}-\\d{7}|\\d{3}-\\d{6}|\\d{4}-\\d{5})|[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7})$", message = "Invalid Email Or Contact No Please Try Again Later")
    private String emailOrCno;
    @NotNull(message = "Please Enter Your Password name!")
    private String password;
    private boolean isGoogle;
    private String img;
    @NotNull(message = "Something Went Wrong!")
    private String type;
    public SignUpAuth() {
    }
    public SignUpAuth(String fName, String lName, String emailOrCno, String password) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.password = password;
    }

    public SignUpAuth(String fName, String lName, String emailOrCno, String password, String img) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.password = password;
        this.img = img;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmailOrCno() {
        return emailOrCno;
    }

    public void setEmailOrCno(String emailOrCno) {
        this.emailOrCno = emailOrCno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsGoogle() {
        return isGoogle;
    }

    public void setIsGoogle(boolean isGoogle) {
        this.isGoogle = isGoogle;
    }

    public boolean isGoogle() {
        return isGoogle;
    }

    public void setGoogle(boolean google) {
        isGoogle = google;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SignUpAuth{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", emailOrCno='" + emailOrCno + '\'' +
                ", password='" + password + '\'' +
                ", isGoogle=" + isGoogle +
                ", img='" + img + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
