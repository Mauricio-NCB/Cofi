package com.website.main.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String url;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Constructores
    public Picture() {}

    public Picture(String url, Post post) {
        this.url = url;
        this.post = post;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
