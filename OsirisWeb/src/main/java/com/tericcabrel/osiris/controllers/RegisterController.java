package com.tericcabrel.osiris.controllers;

import javax.validation.Valid;

import com.tericcabrel.osiris.configs.FileStorageProperties;
import com.tericcabrel.osiris.dtos.UserRegistrationDto;
import com.tericcabrel.osiris.utils.Helpers;
import com.tericcabrel.osiris.models.User;
import com.tericcabrel.osiris.services.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private FileStorageProperties fileStorageProperties;

    private UserService userService;

    public RegisterController(UserService userService, FileStorageProperties fileStorageProperties
    ) {
        this.userService = userService;
        this.fileStorageProperties = fileStorageProperties;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();

        userRegistrationDto.setUid(Helpers.generateRandomString());

        return userRegistrationDto;
    }

    @GetMapping
    public String showRegisterForm(Model model) {
        return "register";
    }

    @PostMapping
    public String registerUser(
            @ModelAttribute("user") @Valid UserRegistrationDto userDto,
            BindingResult result
    ) {

        User existing = userService.findByUid(userDto.getUid());
        if (existing != null) {
            result.rejectValue("uid", null, "There is already an account registered with that uid");
        }

        if (result.hasErrors()) {
            return "register";
        }

        Path fileStorageLocation = Paths.get(this.fileStorageProperties.getUploadDir() + "\\" + userDto.getUid())
                .toAbsolutePath().normalize();

        if(Files.exists(fileStorageLocation)) {
            userDto.setFinger("yes");
        }

        userService.save(userDto);

        // TODO Send confirmation email

        return "redirect:/register?success";
    }
}
