package com.website.main.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDTO {
    
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime datePosted;
    private LocalDateTime dateEdit;
    private String authorName;
    private List<String> tagNames;
    private List<String> pictureUrls;
    private List<CommentResponseDTO> comments;
}
