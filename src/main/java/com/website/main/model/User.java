package com.website.main.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1 = admin, 0 = usuario normal
    @Column(name = "rol_admin", nullable = false)
    private Integer rolAdmin;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "postcode", length = 10)
    private String postcode;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "notified", nullable = false)
    private Boolean notified;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    public User() {}

    // GETTERS Y SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRolAdmin() {
        return rolAdmin;
    }

    public void setRolAdmin(Integer rolAdmin) {
        this.rolAdmin = rolAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    // MÉTODO AUXILIAR OPCIONAL
    public boolean isAdmin() {
        return this.rolAdmin != null && this.rolAdmin == 1;
    }
}

