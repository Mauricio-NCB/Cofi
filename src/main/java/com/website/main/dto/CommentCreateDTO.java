package com.website.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    
    private Integer postId;
    private String content;
    private Integer replyId; // Para respuestas 
}
