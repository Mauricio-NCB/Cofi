package com.website.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_event", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEvent;

    @Column(name = "time_event", nullable = false)
    private LocalTime timeEvent;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "postcode", nullable = false)
    private String postcode;

    // valores posibles: 'cancelado','terminado','proximo','en_curso'
    private String state;

    @Column(name = "chat_id")
    private Integer chatId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
        name = "events_categories",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
        name = "users_events",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    public String getImageUrl() {
        if (categories == null) {
            return "https://picsum.photos/1/200/300?30";
        }

        switch (categories.get(0).getName()) {
            case "Ocio":
                return "https://picsum.photos/id/395/4080/2720";
            case "Cultura":
                return "https://picsum.photos/id/367/4928/326";
            case "Salud":
                return "https://picsum.photos/id/360/1925/1280";
            case "Deportes":
                return "https://picsum.photos/id/541/5000/3181";
            case "Tecnología":
                return "https://picsum.photos/id/8/5000/3333";
            default:
                return "https://picsum.photos/id/491/5000/4061";
        }
    }
}