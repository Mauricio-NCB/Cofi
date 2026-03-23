package com.website.main.controller;

import java.util.List;

import com.website.main.dto.CommentResponseDTO;
import com.website.main.dto.CommentCreateDTO;
import com.website.main.dto.PostResponseDTO;
import com.website.main.dto.PostCreateDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.website.main.service.TagService;
import com.website.main.service.PostService;
import com.website.main.service.CommentService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    public PostController(PostService postService,
                          CommentService commentService,
                          TagService tagService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
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
        if (postDTO.getPictureUrl() != null && !postDTO.getPictureUrl().trim().isEmpty()) {
            String trimmed = postDTO.getPictureUrl().trim();
            
            if (trimmed.length() > 100) return "redirect:/comunidad?error=url_long";
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://"))
                return "redirect:/comunidad?error=url";
        }

        Integer userId = 1; // Temporal hasta login real

        postService.create(postDTO, userId);

        return "redirect:/comunidad";
    }

    @PostMapping("/comentario")
    @ResponseBody
    public void crearComentario(@RequestBody CommentCreateDTO comment) {

        Integer userId = 1; // Temporal hasta login real
        commentService.create(comment, userId);
    }

    @GetMapping("/comentarios/{postId}")
    @ResponseBody
    public List<CommentResponseDTO> getComentarios(@PathVariable Integer postId) {
        return commentService.getCommentsTree(postId);
    }
}