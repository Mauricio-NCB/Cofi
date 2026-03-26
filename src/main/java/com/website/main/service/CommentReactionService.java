package com.website.main.service;

import com.website.main.model.CommentReaction;
import com.website.main.model.CommentReactionId;
import com.website.main.repository.CommentReactionRepository;
import com.website.main.repository.ReactionRepository;
import com.website.main.repository.CommentRepository;
import com.website.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentReactionService {

    private final CommentReactionRepository commentReactionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final AchievementService achievementService;

    public CommentReactionService(CommentReactionRepository commentReactionRepository,
                                  UserRepository userRepository,
                                  CommentRepository commentRepository,
                                  ReactionRepository reactionRepository,
                                  AchievementService achievementService) {
        this.commentReactionRepository = commentReactionRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
        this.achievementService = achievementService;
    }

    // toggle
    public void toggleReaction(Integer userId, Integer commentId, Integer reactionId) throws Exception {
        var reaction = reactionRepository.findById(reactionId).orElseThrow();
        
        // Verificar si es reacción exclusiva y está desbloqueada
        if (reaction.getExclusive()) {
            var unlockedIds = achievementService.getUnlockedReactionIds(userId);
            if (!unlockedIds.contains(reactionId)) {
                throw new Exception("Reacción no desbloqueada");
            }
        }
        
        CommentReactionId id = new CommentReactionId(userId, commentId, reactionId);
        Optional<CommentReaction> existing = commentReactionRepository.findById(id);

        if(existing.isPresent()) {
            commentReactionRepository.delete(existing.get());
        } else {
            CommentReaction cr = new CommentReaction();
            cr.setUser(userRepository.getReferenceById(userId));
            cr.setComment(commentRepository.getReferenceById(commentId));
            cr.setReaction(reaction);
            commentReactionRepository.save(cr);
        }
    }

    public List<CommentReaction> getReactionsByComment(Integer commentId) {
        return commentReactionRepository.findByCommentId(commentId);
    }
}