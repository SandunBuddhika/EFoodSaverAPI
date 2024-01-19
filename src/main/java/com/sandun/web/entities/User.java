package com.sandun.web.entities;

import com.sandun.web.entities.address.Address;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@NamedQueries({
        @NamedQuery(
                name = "signInQ",
                query = "SELECT u FROM User u WHERE  u.emailOrCno = :eorc and u.password=:password and u.userType.name=:type"),
        @NamedQuery(
                name = "getAllUsers",
                query = "SELECT u FROM User u"),
        @NamedQuery(
                name = "checkVerification",
                query = "select u from User u where u.id=:id and u.verificationCode=:code"),
})
@Entity
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fName;
    private String lName;
    @Column(unique = true)
    private String emailOrCno;
    @ManyToOne
    @JoinColumn(nullable = false)
    private UserType userType;
    @OneToOne(mappedBy = "user",cascade = CascadeType.MERGE)
    private Address address;
    @Column(nullable = false)
    private String password;
    private String verificationCode;
    private int status = 0;
    private String img;

    public User() {
    }

    public User(String fName, String lName, String emailOrCno, UserType userType, String password, String verificationCode, int status) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.userType = userType;
        this.password = password;
        this.verificationCode = verificationCode;
        this.status = status;
    }

    public User(String fName, String lName, String emailOrCno, UserType userType, String password, String img, String verificationCode, int status) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.userType = userType;
        this.password = password;
        this.img = img;
        this.verificationCode = verificationCode;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
