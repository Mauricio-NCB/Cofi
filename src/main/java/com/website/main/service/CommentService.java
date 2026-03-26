package com.website.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.website.main.model.Comment;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AchievementService achievementService;

    public CommentService(CommentRepository commentRepository, AchievementService achievementService){
        this.commentRepository = commentRepository;
        this.achievementService = achievementService;
    }

    public Comment findById(Integer id){
        return commentRepository.findById(id).orElseThrow();
    }

    public void save(Comment comment){
        comment.setDateSent(LocalDateTime.now());
        comment.setVisible(true);
        commentRepository.save(comment);
        
        // Desbloquear logro de primer comentario
        if (comment.getUser() != null) {
            achievementService.unlockAchievement(comment.getUser().getId(), AchievementType.FIRST_COMMENT);
        }
    }

    public List<Comment> getCommentsTree(Integer postId){

        List<Comment> all = commentRepository.findAllByPost(postId);

        Map<Integer, Comment> map = new HashMap<>();
        List<Comment> roots = new ArrayList<>();

        for(Comment c : all){
            map.put(c.getId(), c);
            c.setResponses(new ArrayList<>());
        }

        for(Comment c : all){

            if(c.getParent() == null){
                roots.add(c);
            } else {

                Comment parent = map.get(c.getParent().getId());

                if(parent != null){
                    parent.getResponses().add(c);
                }

            }
        }

        return roots;
    }

}