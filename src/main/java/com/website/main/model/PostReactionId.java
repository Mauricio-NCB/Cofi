package com.website.main.model;
import java.io.Serializable;
import java.util.Objects;

public class PostReactionId implements Serializable {

    private Integer user;
    private Integer post;
    private Integer reaction;

    public PostReactionId() {}

    public PostReactionId(Integer user, Integer post, Integer reaction) {
        this.user = user;
        this.post = post;
        this.reaction = reaction;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof PostReactionId)) return false;
        PostReactionId that = (PostReactionId) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(post, that.post) &&
               Objects.equals(reaction, that.reaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post, reaction);
    }
}