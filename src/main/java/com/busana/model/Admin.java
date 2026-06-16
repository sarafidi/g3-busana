package com.busana.model;

import jakarta.persistence.*;


@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @Column(name = "adminID", length = 20, nullable = false)
    private String adminID;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "email", length = 100, nullable = false)
    private String email;
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    //Constructor
    public Admin() {}   

    //Getters and Setters
    public String getAdminID() {
        return adminID;
    }   

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
