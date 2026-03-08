package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.website.main.model.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByTags_NameAndVisibleTrue(String name);

    @Query("""
    SELECT DISTINCT p
    FROM Post p
    LEFT JOIN FETCH p.tags
    WHERE p.visible = true
    ORDER BY p.datePosted DESC
    """)
    List<Post> findAllVisibleWithTags();

}
