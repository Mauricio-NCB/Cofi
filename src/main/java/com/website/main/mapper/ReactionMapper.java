package com.website.main.mapper;

import org.springframework.stereotype.Component;
import com.website.main.dto.Reaction.ReactionResponseDTO;
import com.website.main.dto.Reaction.PostReactionResponseDTO;
import com.website.main.dto.Reaction.CommentReactionResponseDTO;
import com.website.main.model.Reaction;

@Component
public class ReactionMapper {
    
    public ReactionResponseDTO toDTO(Reaction reaction) {
        if (reaction == null) return null;
        
        ReactionResponseDTO dto = new ReactionResponseDTO();
        dto.setId(reaction.getId());
        dto.setName(reaction.getName());
        dto.setEmojiUnicode(reaction.getEmojiUnicode());
        dto.setIconCss(reaction.getIconCss());
        dto.setExclusive(reaction.getExclusive());
        
        return dto;
    }
    
    public ReactionResponseDTO toDTOWithUnlocked(Reaction reaction, Boolean unlocked) {
        ReactionResponseDTO dto = toDTO(reaction);
        if (dto != null) {
            dto.setUnlocked(unlocked);
        }
        return dto;
    }
    
    public PostReactionResponseDTO toPostReactionDTO(Reaction reaction, Integer count, Boolean userReacted) {
        if (reaction == null) return null;
        
        PostReactionResponseDTO dto = new PostReactionResponseDTO();
        dto.setReaction(toDTO(reaction));
        dto.setCount(count);
        dto.setUserReacted(userReacted);
        
        return dto;
    }
    
    public CommentReactionResponseDTO toCommentReactionDTO(Reaction reaction, Integer count, Boolean userReacted) {
        if (reaction == null) return null;
        
        CommentReactionResponseDTO dto = new CommentReactionResponseDTO();
        dto.setReaction(toDTO(reaction));
        dto.setCount(count);
        dto.setUserReacted(userReacted);
        
        return dto;
    }
}
