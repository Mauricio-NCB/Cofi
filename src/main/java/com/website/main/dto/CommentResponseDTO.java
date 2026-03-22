package com.website.main.dto;

import java.util.List;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
    
    private Integer id;
    private String content;
    private LocalDateTime dateSent;
    private LocalDateTime dateEdit;
    private String authorName;
    private List<CommentResponseDTO> replies;
}
