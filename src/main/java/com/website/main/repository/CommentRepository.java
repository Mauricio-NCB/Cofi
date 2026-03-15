package com.website.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.website.main.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{

    @Query("""
        SELECT c
        FROM Comment c
        JOIN FETCH c.user
        WHERE c.post.id = :postId
        AND c.visible = true
        ORDER BY c.dateSent ASC
    """)
    List<Comment> findAllByPost(Integer postId);
}