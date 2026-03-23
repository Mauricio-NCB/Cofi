package com.website.main.mapper;

import org.springframework.stereotype.Component;

import com.website.main.dto.Comment.CommentResponseDTO;
import com.website.main.model.Comment;

@Component
public class CommentMapper {
    
    public CommentResponseDTO toDTO(Comment comment) {

        if (comment == null) return null;

        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setDateSent(comment.getDateSent());
        dto.setDateEdit(comment.getDateEdit());
        dto.setAuthorName(comment.getUser().getName());
        dto.setReplies(
            comment.getReplies() == null ? null :
            comment.getReplies().stream()
                .map(this::toDTO)
                .toList()
        );
        return dto;
    }
}
