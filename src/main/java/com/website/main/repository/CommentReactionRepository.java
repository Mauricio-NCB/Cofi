package com.website.main.repository;
import com.website.main.model.CommentReaction;
import com.website.main.model.CommentReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReactionId> {

    Optional<CommentReaction> findByUserIdAndCommentIdAndReactionId(Integer userId, Integer commentId, Integer reactionId);

    List<CommentReaction> findByCommentId(Integer commentId);
}