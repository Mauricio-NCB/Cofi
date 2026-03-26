package com.website.main.dto.Reaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReactionResponseDTO {
    
    private ReactionResponseDTO reaction;
    private Integer count;
    private Boolean userReacted;
}
