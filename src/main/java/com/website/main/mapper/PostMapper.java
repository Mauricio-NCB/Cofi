package com.website.main.mapper;

import com.website.main.model.Post;
import com.website.main.model.Tag;
import com.website.main.dto.Post.PostResponseDTO;
import com.website.main.model.Picture;

public class PostMapper {
    
    public PostResponseDTO toDTO(Post post) {
        
        if (post == null) return null;
        
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setDatePosted(post.getDatePosted());
        dto.setDateEdit(post.getDateEdit());
        dto.setAuthorName(post.getUser().getName());
        dto.setTagNames(
            post.getTags().stream()
                .map(Tag::getName)
                .toList()
        );
        dto.setPictureUrls(
            post.getPictures().stream()
                .map(Picture::getUrl)
                .toList()
        );
        
        return dto;
    }
}
