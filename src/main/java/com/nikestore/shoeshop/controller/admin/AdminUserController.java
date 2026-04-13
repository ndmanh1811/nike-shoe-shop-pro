package com.nikestore.shoeshop.controller.admin;

import com.nikestore.shoeshop.dto.UserForm;
import com.nikestore.shoeshop.entity.AppRole;
import com.nikestore.shoeshop.entity.AppUser;
import com.nikestore.shoeshop.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserRepository userRepository;

    public AdminUserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        AppUser user = userRepository.findById(id).orElseThrow();

        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setEmail(user.getEmail());
        form.setFullName(user.getFullName());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        form.setRole(user.getRole().name());
        form.setEnabled(user.isEnabled());

        model.addAttribute("form", form);
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") UserForm form) {
        AppUser user = userRepository.findById(form.getId()).orElseThrow();

        user.setFullName(form.getFullName());
        user.setPhone(form.getPhone());
        user.setAddress(form.getAddress());
        user.setRole(AppRole.valueOf(form.getRole()));
        user.setEnabled(form.isEnabled());

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    // 🔥 TOGGLE KHÓA USER NHANH
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        AppUser user = userRepository.findById(id).orElseThrow();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return "redirect:/admin/users";
    }
}