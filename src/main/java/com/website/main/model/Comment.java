package com.website.main.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_sent")
    private LocalDateTime dateSent;

    @Column(name = "date_edit")
    private LocalDateTime dateEdit;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    private Boolean visible;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "response_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> replies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "post_id")
    private Post post;

}