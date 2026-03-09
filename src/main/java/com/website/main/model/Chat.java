package com.website.main.model; 

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    @ManyToMany
    @JoinTable(
        name = "users_chats",
        joinColumns = @JoinColumn(name = "chat_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private List<User> users;
}
