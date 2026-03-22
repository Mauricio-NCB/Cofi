package com.website.main.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCreateDTO {

    private String type;
    private List<String> participantNames;
    private Integer creatorId;
    
}
