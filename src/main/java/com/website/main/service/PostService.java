package com.website.main.service;

import org.springframework.stereotype.Service;
import com.website.main.model.Post;
import com.website.main.model.User;
import com.website.main.model.Picture;
import com.website.main.repository.PostRepository;
import com.website.main.repository.UserRepository;
import com.website.main.repository.PictureRepository;
import java.time.LocalDateTime;
import java.util.List;
import com.website.main.model.Tag;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, PictureRepository pictureRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
    }

    public Post findById(Integer id){
        return postRepository.findById(id).orElseThrow();
    }

    public List<Post> findByTag(String tag) {
        return postRepository.findByTags_NameAndVisibleTrue(tag);
    }

    public List<Post> findAllVisible() {
        return postRepository.findAllVisibleWithTags();
    }

    public Post create(String title, String content, Integer userId,  List<Tag> tags, String imageUrl) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDatePosted(LocalDateTime.now());
        post.setVisible(true);
        post.setUser(user);
        if(tags!=null && !tags.isEmpty()) {
            if (tags.size() <= 3) {
                post.setTags(tags);
            } else {
                throw new RuntimeException("Máximo 3 tags permitidos");
            }
        }

        Post savedPost = postRepository.save(post);

        // Guardar la imagen si se proporciona
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Picture picture = new Picture(imageUrl.trim(), savedPost);
            pictureRepository.save(picture);
        }

        return savedPost;
    }
}