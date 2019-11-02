package com.tericcabrel.osiris.controllers;

import com.tericcabrel.osiris.models.User;
import com.tericcabrel.osiris.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {
    UserRepository userRepository;

    public IndexController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping({"", "/", "/index"})
    public String indexPage(Model model) {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();

        model.addAttribute("users", users);
        return "index";
    }
}
