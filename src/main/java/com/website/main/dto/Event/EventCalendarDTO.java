package com.website.main.dto.Event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCalendarDTO {
    
    private Integer id;
    private String title;
    private String start;
    private String description;
    private String dateEvent;
    private String timeEvent;
    private Integer maxCapacity;
    private String state;
}
