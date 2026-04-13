package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.dto.UserRegistrationForm;
import com.nikestore.shoeshop.entity.AppRole;
import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signin")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("form", new UserRegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") UserRegistrationForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "auth/register";
        }

        AppUser user = new AppUser();
        user.setEmail(form.getEmail());
        user.setFullName(form.getFullName());
        user.setPhone(form.getPhone());
        user.setAddress(form.getAddress());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(AppRole.ROLE_USER);
        user.setEnabled(true);

        userRepository.save(user);
        return "redirect:/signin?registered=true";
    }
}