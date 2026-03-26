package com.website.main.dto.Reaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionResponseDTO {
    
    private Integer id;
    private String name;
    private String emojiUnicode;
    private String iconCss;
    private Boolean exclusive;
    private Boolean unlocked;
}
