package com.website.main.service;

import com.website.main.model.PostReaction;
import com.website.main.model.PostReactionId;
import com.website.main.repository.PostReactionRepository;
import com.website.main.repository.ReactionRepository;
import com.website.main.repository.PostRepository;
import com.website.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostReactionService {

    private final PostReactionRepository postReactionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;
    private final AchievementService achievementService;

    public PostReactionService(PostReactionRepository postReactionRepository,
                               UserRepository userRepository,
                               PostRepository postRepository,
                               ReactionRepository reactionRepository,
                               AchievementService achievementService) {
        this.postReactionRepository = postReactionRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reactionRepository = reactionRepository;
        this.achievementService = achievementService;
    }

    // toggle
    public void toggleReaction(Integer userId, Integer postId, Integer reactionId) throws Exception {
        var reaction = reactionRepository.findById(reactionId).orElseThrow();
        
        // Verificar si es reacción exclusiva y está desbloqueada
        if (reaction.getExclusive()) {
            var unlockedIds = achievementService.getUnlockedReactionIds(userId);
            if (!unlockedIds.contains(reactionId)) {
                throw new Exception("Reacción no desbloqueada");
            }
        }
        
        PostReactionId id = new PostReactionId(userId, postId, reactionId);
        Optional<PostReaction> existing = postReactionRepository.findById(id);

        if(existing.isPresent()) {
            postReactionRepository.delete(existing.get());
        } else {
            PostReaction pr = new PostReaction();
            pr.setUser(userRepository.getReferenceById(userId));
            pr.setPost(postRepository.getReferenceById(postId));
            pr.setReaction(reaction);
            postReactionRepository.save(pr);
        }
    }

    public List<PostReaction> getReactionsByPost(Integer postId) {
        return postReactionRepository.findByPostId(postId);
    }
}
