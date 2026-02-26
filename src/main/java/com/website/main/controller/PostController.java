package com.website.main.controller;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.website.main.service.PostService;

@Controller
@RequestMapping("/comunidad")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String comunidad(Model model) {
        model.addAttribute("posts", postService.findAllVisible());
        return "comunidad";
    }

    @PostMapping("/crear")
    public String crearPost(@RequestParam String title,
                            @RequestParam String content) {

        Integer userId = 1; // Temporal hasta login real
        postService.create(title, content, userId);

        return "redirect:/comunidad";
    }
}
