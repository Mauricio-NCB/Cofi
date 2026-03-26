package com.website.main.service;

import com.website.main.repository.ReactionRepository;
import com.website.main.repository.PostReactionRepository;
import com.website.main.repository.CommentReactionRepository;
import com.website.main.model.Reaction;
import com.website.main.model.PostReaction;
import com.website.main.model.CommentReaction;
import com.website.main.dto.Reaction.ReactionResponseDTO;
import com.website.main.dto.Reaction.PostReactionResponseDTO;
import com.website.main.dto.Reaction.CommentReactionResponseDTO;
import com.website.main.mapper.ReactionMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostReactionRepository postReactionRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final ReactionMapper reactionMapper;
    private final AchievementService achievementService;

    public ReactionService(ReactionRepository reactionRepository,
                           PostReactionRepository postReactionRepository,
                           CommentReactionRepository commentReactionRepository,
                           ReactionMapper reactionMapper,
                           AchievementService achievementService) {
        this.reactionRepository = reactionRepository;
        this.postReactionRepository = postReactionRepository;
        this.commentReactionRepository = commentReactionRepository;
        this.reactionMapper = reactionMapper;
        this.achievementService = achievementService;
    }

    public List<Reaction> findAll() {
        return reactionRepository.findAll();
    }

    // Obtener todas las reacciones con estado de desbloqueada
    public List<ReactionResponseDTO> getAllReactionsWithUnlocked(Integer userId) {
        List<Reaction> allReactions = reactionRepository.findAll();
        List<Integer> unlockedIds = achievementService.getUnlockedReactionIds(userId);
        
        return allReactions.stream()
            .map(r -> reactionMapper.toDTOWithUnlocked(r, !r.getExclusive() || unlockedIds.contains(r.getId())))
            .collect(Collectors.toList());
    }
    
    // Obtener reacciones de un post con información completa
    public List<PostReactionResponseDTO> getPostReactionsWithDetails(Integer postId, Integer userId) {
        List<Reaction> allReactions = reactionRepository.findAll();
        List<PostReaction> postReactions = postReactionRepository.findByPostId(postId);
        List<Integer> unlockedIds = achievementService.getUnlockedReactionIds(userId);
        
        return allReactions.stream()
            .map(reaction -> {
                Integer count = (int) postReactions.stream()
                    .filter(pr -> pr.getReaction().getId().equals(reaction.getId()))
                    .count();
                
                Boolean userReacted = postReactions.stream()
                    .anyMatch(pr -> pr.getReaction().getId().equals(reaction.getId()) && 
                                   pr.getUser().getId().equals(userId));
                
                PostReactionResponseDTO dto = reactionMapper.toPostReactionDTO(reaction, count, userReacted);
                
                // Marcar si está desbloqueada
                Boolean unlocked = !reaction.getExclusive() || unlockedIds.contains(reaction.getId());
                dto.getReaction().setUnlocked(unlocked);
                
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Obtener reacciones de un comentario con información completa
    public List<CommentReactionResponseDTO> getCommentReactionsWithDetails(Integer commentId, Integer userId) {
        List<Reaction> allReactions = reactionRepository.findAll();
        List<CommentReaction> commentReactions = commentReactionRepository.findByCommentId(commentId);
        List<Integer> unlockedIds = achievementService.getUnlockedReactionIds(userId);
        
        return allReactions.stream()
            .map(reaction -> {
                Integer count = (int) commentReactions.stream()
                    .filter(cr -> cr.getReaction().getId().equals(reaction.getId()))
                    .count();
                
                Boolean userReacted = commentReactions.stream()
                    .anyMatch(cr -> cr.getReaction().getId().equals(reaction.getId()) && 
                                   cr.getUser().getId().equals(userId));
                
                CommentReactionResponseDTO dto = reactionMapper.toCommentReactionDTO(reaction, count, userReacted);
                
                // Marcar si está desbloqueada
                Boolean unlocked = !reaction.getExclusive() || unlockedIds.contains(reaction.getId());
                dto.getReaction().setUnlocked(unlocked);
                
                return dto;
            })
            .collect(Collectors.toList());
    }
}
