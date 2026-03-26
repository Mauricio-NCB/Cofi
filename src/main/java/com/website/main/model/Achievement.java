package com.website.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @ManyToOne
    @JoinColumn(name = "unlocked_reaction_id")
    private Reaction unlockedReaction;

    public enum AchievementType {
        FIRST_POST,           // Primer post
        FIRST_EVENT,          // Primer evento
        FIRST_COMMENT         // Primer comentario
    }
}
