package com.website.main.dto.Event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventResponseDTO {

    private Integer id;
    private LocalDate dateEvent;
    private LocalTime timeEvent;
    private String title;
    private String description;
    private Integer maxCapacity;
    private Integer availableSpots;
    private String postcode;
    private String state;
    private Integer chatId;
    private Integer creatorId;
    private String creatorName;
    private List<String> categoryNames;
    private String imageUrl;
    private Boolean isUserParticipant; // true si el usuario actual participa en el evento
}
