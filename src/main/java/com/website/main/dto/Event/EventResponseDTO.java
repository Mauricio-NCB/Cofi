package com.website.main.dto.Event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.website.main.dto.User.UserParticipantDTO;

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
    private Boolean isUserCreator;     // true si el usuario actual es el creador
    private List<UserParticipantDTO> participants;
}
