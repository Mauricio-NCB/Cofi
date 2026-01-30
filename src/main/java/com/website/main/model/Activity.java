package com.website.main.model;

public class Activity {
    private String title;
    private String author;
    private String tag;
    private String imageUrl;

    public Activity(String title, String author, String tag, String imageUrl) {
        this.title = title;
        this.author = author;
        this.tag = tag;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getTag() { return tag; }
    public String getImageUrl() { return imageUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setTag(String tag) { this.tag = tag; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}