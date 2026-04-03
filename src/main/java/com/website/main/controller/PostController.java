package com.website.main.controller;

import java.util.List;
import java.util.Map;

import com.website.main.dto.Comment.CommentCreateDTO;
import com.website.main.dto.Comment.CommentResponseDTO;
import com.website.main.dto.Post.PostCreateDTO;
import com.website.main.dto.Post.PostResponseDTO;
import com.website.main.dto.Reaction.ReactionResponseDTO;
import com.website.main.dto.Reaction.PostReactionResponseDTO;
import com.website.main.dto.Reaction.CommentReactionResponseDTO;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.website.main.service.CommentReactionService;
import com.website.main.service.TagService;
import com.website.main.service.PostService;
import com.website.main.service.PostReactionService;
import com.website.main.service.AchievementService;
import com.website.main.service.CommentService;
import com.website.main.service.ReactionService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final PostReactionService postReactionService;
    private final CommentReactionService commentReactionService;
    private final AchievementService achievementService;
    private final ReactionService reactionService;

    public PostController(PostService postService,
                          CommentService commentService,
                          TagService tagService,
                          PostReactionService postReactionService,
                          CommentReactionService commentReactionService,
                          AchievementService achievementService,
                          ReactionService reactionService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.postReactionService = postReactionService;
        this.commentReactionService = commentReactionService;
        this.reactionService = reactionService;
        this.achievementService = achievementService;
    }

    @GetMapping
    public String comunidad(@RequestParam(required = false) String tag,
                            Model model) {

        List<PostResponseDTO> posts;

        if (tag != null && !tag.isBlank()) {
            posts = postService.findByTag(tag);
            model.addAttribute("selectedTag", tag);
        } else {
            posts = postService.findAllVisible();
        }

        model.addAttribute("posts", posts);
        model.addAttribute("popularTags", tagService.findPopularTags());
        model.addAttribute("currentPage", "comunidad");

        return "comunidad";
    }

    @PostMapping("/crear")
    public String crearPost(@ModelAttribute PostCreateDTO postDTO) {

        // Validar URL si se proporciona en la request
        if (postDTO.getImageUrl() != null && !postDTO.getImageUrl().trim().isEmpty()) {
            String trimmed = postDTO.getImageUrl().trim();
            
            if (trimmed.length() > 100) return "redirect:/comunidad?error=url_long";
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://"))
                return "redirect:/comunidad?error=url";
        }

        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        postService.create(postDTO, userId);

        return "redirect:/comunidad";
    }

    @PostMapping("/comentario")
    @ResponseBody
    public void crearComentario(@RequestBody CommentCreateDTO comment) {

        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        commentService.create(comment, userId);
    }

    @GetMapping("/comentarios/{postId}")
    @ResponseBody
    public List<CommentResponseDTO> getComentarios(@PathVariable Integer postId) {
        return commentService.getCommentsTree(postId);
    }

    @PostMapping("/react")
    @ResponseBody
    public Map<String, String> reactToPost(@RequestBody Map<String, Integer> body) {
        Integer postId = body.get("postId");
        Integer reactionId = body.get("reactionId");
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        try {
            postReactionService.toggleReaction(userId, postId, reactionId);
            return Map.of("status", "success");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/reacciones/disponibles")
    @ResponseBody
    public List<ReactionResponseDTO> getAllReactions() {
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return reactionService.getAllReactionsWithUnlocked(userId);
    }

    @GetMapping("/reacciones/{postId}")
    @ResponseBody
    public List<PostReactionResponseDTO> getReactions(@PathVariable Integer postId) {
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return reactionService.getPostReactionsWithDetails(postId, userId);
    }

    @PostMapping("/react/comment")
    @ResponseBody
    public Map<String, String> reactToComment(@RequestBody Map<String, Integer> body) {
        Integer commentId = body.get("commentId");
        Integer reactionId = body.get("reactionId");
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        try {
            commentReactionService.toggleReaction(userId, commentId, reactionId);
            return Map.of("status", "success");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/reacciones/comment/{commentId}")
    @ResponseBody
    public List<CommentReactionResponseDTO> getCommentReactions(@PathVariable Integer commentId) {
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return reactionService.getCommentReactionsWithDetails(commentId, userId);
    }

    @GetMapping("/reacciones/desbloqueadas")
    @ResponseBody
    public List<Integer> getUnlockedReactions() {
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return achievementService.getUnlockedReactionIds(userId);
    }
}