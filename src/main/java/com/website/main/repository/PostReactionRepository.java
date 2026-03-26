package com.website.main.repository;
import com.website.main.model.PostReaction;
import com.website.main.model.PostReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, PostReactionId> {

    Optional<PostReaction> findByUserIdAndPostIdAndReactionId(Integer userId, Integer postId, Integer reactionId);

    List<PostReaction> findByPostId(Integer postId);
}