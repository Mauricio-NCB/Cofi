package com.website.main.model;
import java.io.Serializable;
import java.util.Objects;

public class CommentReactionId implements Serializable {

    private Integer user;
    private Integer comment;
    private Integer reaction;

    public CommentReactionId() {}

    public CommentReactionId(Integer user, Integer comment, Integer reaction) {
        this.user = user;
        this.comment = comment;
        this.reaction = reaction;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CommentReactionId)) return false;
        CommentReactionId that = (CommentReactionId) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(comment, that.comment) &&
               Objects.equals(reaction, that.reaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, comment, reaction);
    }
}