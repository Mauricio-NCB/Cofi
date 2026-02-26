package com.website.main.service;

import org.springframework.stereotype.Service;
import com.website.main.model.Post;
import com.website.main.model.User;
import com.website.main.repository.PostRepository;
import com.website.main.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> findAllVisible() {
        return postRepository.findByVisibleTrueOrderByDatePostedDesc();
    }

    public Post create(String title, String content, Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDatePosted(LocalDateTime.now());
        post.setVisible(true);
        post.setUser(user);

        return postRepository.save(post);
    }
}