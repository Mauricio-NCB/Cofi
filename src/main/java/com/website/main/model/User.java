package com.website.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "postcode", length = 10)
    private String postcode;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "notified", nullable = false)
    private Boolean notified;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @ManyToMany(mappedBy = "participants")
    private List<Event> events;

    @ManyToMany(mappedBy = "users")
    private List<Chat> chats;

    @ManyToMany
    @JoinTable(
        name = "users_categories",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> preferedCategories;

}

