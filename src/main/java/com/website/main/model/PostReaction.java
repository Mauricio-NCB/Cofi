package com.website.main.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="posts_reactions")
@IdClass(PostReactionId.class)
public class PostReaction {

    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Id
    @ManyToOne
    @JsonIgnore //CAMBIAR CON DTO (?)
    @JoinColumn(name="post_id")
    private Post post;

    @Id
    @ManyToOne
    @JoinColumn(name="reaction_id")
    private Reaction reaction;
}
