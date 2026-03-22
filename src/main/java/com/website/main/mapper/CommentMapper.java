package com.website.main.mapper;

import com.website.main.model.Comment;
import com.website.main.dto.CommentResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentMapper {
    
    public CommentResponseDTO toDTO(Comment comment) {
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
