package com.website.main.controller;

import java.util.ArrayList;
import java.util.List;

import com.website.main.model.Comment;
import com.website.main.model.User;

import org.springframework.stereotype.Controller;
import com.website.main.service.CommentService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.website.main.model.Post;
import com.website.main.model.Tag;
import com.website.main.repository.TagRepository;
import com.website.main.service.PostService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagRepository tagRepository;

    public PostController(PostService postService,
                          CommentService commentService,
                          TagRepository tagRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagRepository = tagRepository;
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
        model.addAttribute("popularTags", tagRepository.findPopularTags());
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

                    Tag tag = tagRepository.findByName(name)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(normalizedName);
                                return tagRepository.save(newTag);
                            });

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
}