package com.website.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.website.main.model.Comment;
import com.website.main.model.User;
import com.website.main.model.Tag;

import org.springframework.stereotype.Controller;
import com.website.main.service.CommentService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.website.main.model.Post;
import com.website.main.model.Reaction;
import com.website.main.model.PostReaction;
import com.website.main.model.CommentReaction;
import com.website.main.service.CommentReactionService;
import com.website.main.service.TagService;
import com.website.main.service.PostService;
import com.website.main.service.PostReactionService;
import com.website.main.service.ReactionService;
import com.website.main.service.AchievementService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;
    private final ReactionService reactionService;
    private final PostReactionService postReactionService;
    private final CommentReactionService commentReactionService;
    private final AchievementService achievementService;

    public PostController(PostService postService,
                          CommentService commentService,
                          TagService tagService,
                          ReactionService reactionService,
                          PostReactionService postReactionService,
                          CommentReactionService commentReactionService,
                          AchievementService achievementService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.reactionService = reactionService;
        this.postReactionService = postReactionService;
        this.commentReactionService = commentReactionService;
        this.achievementService = achievementService;
    }

    @GetMapping
    public String comunidad(@RequestParam(required = false) String tag,
                            Model model) {

        List<Post> posts;

        if (tag != null && !tag.isBlank()) {
            posts = postService.findByTag(tag);
            model.addAttribute("selectedTag", tag);
        } else {
            posts = postService.findAllVisible();
        }

        // CARGAR COMENTARIOS PARA CADA POST
        for (Post p : posts) {
            p.setComments(commentService.getCommentsTree(p.getId()));
        }
        model.addAttribute("posts", posts);
        model.addAttribute("popularTags", tagService.findPopularTags());
        model.addAttribute("currentPage", "comunidad");

        return "comunidad";
    }

    @PostMapping("/crear")
    public String crearPost(@RequestParam String title,
                            @RequestParam String content,
                            @RequestParam(required = false) String tags,
                            @RequestParam(required = false) String imageUrl) {

        // Validar URL si se proporciona
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            String trimmed = imageUrl.trim();
            if (trimmed.length() > 100) {
                return "redirect:/comunidad?error=url_long";
            }
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                return "redirect:/comunidad?error=url";
            }
        }

        Integer userId = 1; // Temporal hasta login real

        List<Tag> tagList = new ArrayList<>();

        if (tags != null && !tags.trim().isEmpty()) {

            String[] tagNames = tags.split(",");

            if (tagNames.length > 3) {
                throw new RuntimeException("Máximo 3 tags permitidos");
            }

            for (String name : tagNames) {

                name = name.trim().toLowerCase();
                String normalizedName = name.trim().toLowerCase();

                if (!name.isEmpty()) {
                    Tag tag = tagService.findOrCreate(normalizedName);   
                    tagList.add(tag);
                }
            }
        }

        postService.create(title, content, userId, tagList, imageUrl);

        return "redirect:/comunidad";
    }

    @PostMapping("/comentario")
    @ResponseBody
    public void crearComentario(@RequestParam Integer postId,
                                @RequestParam String content,
                                @RequestParam(required = false) Integer parentId){

        Comment comment = new Comment();

        comment.setContent(content);

        Post post = postService.findById(postId);
        comment.setPost(post);

        User user = new User();
        user.setId(1);
        comment.setUser(user);

        if(parentId != null){
            Comment parent = commentService.findById(parentId);
            comment.setParent(parent);
        }

        commentService.save(comment);
    }

    @GetMapping("/comentarios/{postId}")
    @ResponseBody
    public List<Comment> getComentarios(@PathVariable Integer postId) {
        return commentService.getCommentsTree(postId);
    }

    @PostMapping("/react")
    @ResponseBody
    public Map<String, String> reactToPost(@RequestBody Map<String, Integer> body) {
        Integer postId = body.get("postId");
        Integer reactionId = body.get("reactionId");
        Integer userId = 1; // temporal hasta login real

        try {
            postReactionService.toggleReaction(userId, postId, reactionId);
            return Map.of("status", "success");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/reacciones/disponibles")
    @ResponseBody
    public List<Reaction> getAllReactions() {
        return reactionService.findAll();
    }

    @GetMapping("/reacciones/{postId}")
    @ResponseBody
    public List<PostReaction> getReactions(@PathVariable Integer postId) {
        return postReactionService.getReactionsByPost(postId);
    }

    @PostMapping("/react/comment")
    @ResponseBody
    public Map<String, String> reactToComment(@RequestBody Map<String, Integer> body) {
        Integer commentId = body.get("commentId");
        Integer reactionId = body.get("reactionId");
        Integer userId = 1; // temporal hasta login real

        try {
            commentReactionService.toggleReaction(userId, commentId, reactionId);
            return Map.of("status", "success");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/reacciones/comment/{commentId}")
    @ResponseBody
    public List<CommentReaction> getCommentReactions(@PathVariable Integer commentId) {
        return commentReactionService.getReactionsByComment(commentId);
    }

    @GetMapping("/reacciones/desbloqueadas")
    @ResponseBody
    public List<Integer> getUnlockedReactions() {
        Integer userId = 1; // temporal hasta login real
        return achievementService.getUnlockedReactionIds(userId);
    }
}