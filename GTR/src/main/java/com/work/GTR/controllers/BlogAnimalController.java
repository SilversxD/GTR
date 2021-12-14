package com.work.GTR.controllers;

import com.work.GTR.services.HeaderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import com.work.GTR.models.Post;
import com.work.GTR.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class BlogAnimalController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HeaderService headerService;

    @GetMapping("/blog")
    public String blogAnimal(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("addNews", headerService.isUser());
        return "blog-car";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/add")
    public String blogAdd(Model model) {
        model.addAttribute("addNews", headerService.isUser());
        return "blog-add";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/add")
    public String blogPostAdd(@RequestParam String breed, @RequestParam String name,@RequestParam String info,@RequestParam String full_text, Model model) {
        Post post = new Post(breed, name, info, full_text);
        postRepository.save(post);
        return "redirect:/blog";

    }

    @GetMapping("/blog/{id}")
    public String blogDetails(@PathVariable(value = "id") long id, Model model) {

        model.addAttribute("addNews", headerService.isUser());
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals("admin")) {
            model.addAttribute("dellForm", "KEKW");
        }
        else {
            model.addAttribute("dellForm", "KEKL");
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);


        return "blog-details";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Model model) {

        model.addAttribute("addNews", headerService.isUser());
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);

        return "blog-edit";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/blog/{id}/edit")
    public String blogPostUpdate(@PathVariable(value = "id") long id, @RequestParam String breed, @RequestParam String name,@RequestParam String info,@RequestParam String full_text, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setBreed(breed);
        post.setName(name);
        post.setInfo(info);
        post.setFull_text(full_text);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/blog/{id}/remove")
    public String blogPostDelete(@PathVariable(value = "id") long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/blog";
    }

}
