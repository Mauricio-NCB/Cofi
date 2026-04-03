package com.website.main.dto.Post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {

    private String title;
    private String content;
    private String tags; // Coma-separated tags
    private String imageUrl;
    
}
