package com.website.main.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="comments_reactions")
@IdClass(CommentReactionId.class)
public class CommentReaction {

    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="comment_id")
    private Comment comment;

    @Id
    @ManyToOne
    @JoinColumn(name="reaction_id")
    private Reaction reaction;
}