package com.website.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.website.main.model.Comment;
import com.website.main.model.Post;
import com.website.main.model.User;
import com.website.main.dto.CommentCreateDTO;
import com.website.main.dto.CommentResponseDTO;
import com.website.main.mapper.CommentMapper;
import com.website.main.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper){
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;

    }

    public CommentResponseDTO findById(Integer id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        return commentMapper.toDTO(comment);
    }

    public CommentResponseDTO create(CommentCreateDTO comment, Integer userId) {

        Comment newComment = new Comment();
        newComment.setContent(comment.getContent());
        newComment.setDateSent(LocalDateTime.now());
        newComment.setVisible(true);

        Post post = new Post();
        post.setId(comment.getPostId());
        newComment.setPost(post);

        User user = new User();
        user.setId(userId);
        newComment.setUser(user);

        Comment savedComment = commentRepository.save(newComment);

        return commentMapper.toDTO(savedComment);
    }

    public List<CommentResponseDTO> getCommentsTree(Integer postId){

        List<Comment> all = commentRepository.findAllByPost(postId);

        Map<Integer, Comment> map = new HashMap<>();
        List<Comment> roots = new ArrayList<>();

        for(Comment c : all){
            map.put(c.getId(), c);
            c.setReplies(new ArrayList<>());
        }

        for(Comment c : all){

            if(c.getParent() == null){
                roots.add(c);
            } else {

                Comment parent = map.get(c.getParent().getId());

                if(parent != null){
                    parent.getReplies().add(c);
                }

            }
        }

        return roots.stream()
                .map(commentMapper::toDTO)
                .toList();
    }

}