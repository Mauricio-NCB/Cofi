package com.website.main.dto.Event;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCreateDTO {
    
    private LocalDate dateEvent;
    private LocalTime timeEvent;
    private String title;
    private String description;
    private Integer maxCapacity;
    private String postcode;
}
