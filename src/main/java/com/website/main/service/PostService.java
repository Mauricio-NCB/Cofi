package com.website.main.service;

import org.springframework.stereotype.Service;
import com.website.main.model.Post;
import com.website.main.model.User;
import com.website.main.model.Picture;
import com.website.main.model.Tag;
import com.website.main.dto.Post.PostCreateDTO;
import com.website.main.dto.Post.PostResponseDTO;
import com.website.main.mapper.PostMapper;
import com.website.main.repository.PostRepository;
import com.website.main.repository.UserRepository;
import com.website.main.repository.PictureRepository;
import com.website.main.repository.TagRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, UserRepository userRepository, 
            PictureRepository pictureRepository, TagRepository tagRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.tagRepository = tagRepository;
        this.postMapper = postMapper;
    }

    public PostResponseDTO findById(Integer id){

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        return postMapper.toDTO(post);
    }

    public List<PostResponseDTO> findByTag(String tag) {
        return postRepository.findByTags_NameAndVisibleTrue(tag).stream()
                .map(postMapper::toDTO)
                .toList();
    }

    public List<PostResponseDTO> findAllVisible() {
        return postRepository.findAllVisibleWithTags().stream()
                .map(postMapper::toDTO)
                .toList();
    }

    public PostResponseDTO create(PostCreateDTO postDTO, Integer userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        List<Tag> tagList = new ArrayList<>();
        if (postDTO.getTags() != null && !postDTO.getTags().trim().isEmpty()) {
            String[] tagNames = postDTO.getTags().split(",");
            
            if (tagNames.length > 3) throw new RuntimeException("Máximo 3 tags permitidos");

            for (String name : tagNames) {
                name = name.trim().toLowerCase();
                String normalizedName = name.trim().toLowerCase();

                if (!normalizedName.isEmpty()) {
                    Tag tag = tagRepository.findByName(normalizedName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(normalizedName);
                                return tagRepository.save(newTag);
                            });
                    tagList.add(tag);
                }
            }
        }

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setDatePosted(LocalDateTime.now());
        post.setVisible(true);
        post.setUser(user);
        if (!tagList.isEmpty()) post.setTags(tagList);

        Post savedPost = postRepository.save(post);

        // Guardar la imagen si se proporciona
        if (postDTO.getPictureUrl() != null && !postDTO.getPictureUrl().trim().isEmpty()) {
            Picture picture = new Picture(postDTO.getPictureUrl().trim(), savedPost);
            pictureRepository.save(picture);
        }

        return postMapper.toDTO(savedPost);
    }
}