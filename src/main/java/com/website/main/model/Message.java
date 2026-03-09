package com.website.main.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_sent", nullable = false)
    private LocalDateTime dateSent;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToOne
    @JoinColumn(name = "response_id")
    private Message response;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
