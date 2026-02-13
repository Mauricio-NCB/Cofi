package com.website.main.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    // valores posibles: 'cancelado','terminado','proximo','en_curso'
    private String state;

    @Column(name = "chat_id")
    private Integer chatId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Event() {}

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}