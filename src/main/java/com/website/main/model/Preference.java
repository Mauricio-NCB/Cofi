package com.website.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "preferences")
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "text_size_level")
    private Integer textSizeLevel;

    @Column(name = "button_size_level")
    private Integer buttonSizeLevel;

    @Column(name = "menu_main_color")
    private Integer menuMainColor;

    @Column(name = "menu_secondary_color")
    private Integer menuSecondaryColor;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    public Preference(Integer userId) {
        this.userId = userId;
    }
}
