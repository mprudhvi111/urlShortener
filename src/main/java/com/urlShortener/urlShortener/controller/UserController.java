package com.urlShortener.urlShortener.controller;

import com.urlShortener.urlShortener.dtos.RegisterUserForm;
import com.urlShortener.urlShortener.models.CreateUserCmd;
import com.urlShortener.urlShortener.models.Role;
import com.urlShortener.urlShortener.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/register")
    public String register(Model model)
    {
        model.addAttribute("user", new RegisterUserForm("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterUserForm registerUserForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes)
    {
        if(bindingResult.hasErrors())
        {
            return "register";
        }
        try {
            var userCmd = new CreateUserCmd(registerUserForm.email(),
                    registerUserForm.name(),
                    registerUserForm.password(),
                            Role.ROLE_USER);
            userService.createUser(userCmd);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed! " + e.getMessage());
            return "redirect:/register";
        }

    }
}
