package com.website.main.dto.Chat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDTO {
    
    private Integer id;
    private String name;
    private List<String> participantNames;
}
