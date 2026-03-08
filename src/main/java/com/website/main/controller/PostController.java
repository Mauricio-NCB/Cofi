package com.website.main.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.website.main.model.Tag;
import com.website.main.repository.TagRepository;
import com.website.main.service.PostService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;
    private final TagRepository tagRepository;

    public PostController(PostService postService,
                          TagRepository tagRepository) {
        this.postService = postService;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public String comunidad(@RequestParam(required = false) String tag,
                            Model model) {

        if (tag != null && !tag.isBlank()) {
            model.addAttribute("posts", postService.findByTag(tag));
            model.addAttribute("selectedTag", tag);
        } else {
            model.addAttribute("posts", postService.findAllVisible());
        }

        model.addAttribute("popularTags", tagRepository.findPopularTags());
        model.addAttribute("currentPage", "comunidad");

        return "comunidad";
    }

    @PostMapping("/crear")
    public String crearPost(@RequestParam String title,
                            @RequestParam String content,
                            @RequestParam(required = false) String tags) {

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

        postService.create(title, content, userId, tagList);

        return "redirect:/comunidad";
    }
}