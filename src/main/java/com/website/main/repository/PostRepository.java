package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.main.model.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByVisibleTrueOrderByDatePostedDesc();

}
