package com.website.main.dto.Chat;

import com.website.main.dto.User.UserParticipantDTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCreateDTO {

    private String name;
    private List<UserParticipantDTO> participants;
    private Integer creatorId;
    
}
