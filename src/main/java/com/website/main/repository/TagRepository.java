package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.website.main.model.Tag;
import java.util.List;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByName(String name);

    @Query("""
    SELECT t.name, COUNT(p.id) 
    FROM Tag t 
    LEFT JOIN t.posts p
    GROUP BY t.name
    ORDER BY COUNT(p.id) DESC
    """)
    List<Object[]> findPopularTags();

}