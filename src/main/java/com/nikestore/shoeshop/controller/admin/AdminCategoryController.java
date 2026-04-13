package com.nikestore.shoeshop.controller.admin;

import com.nikestore.shoeshop.dto.CategoryForm;
import com.nikestore.shoeshop.entity.Category;
import com.nikestore.shoeshop.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    public AdminCategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAscNameAsc());
        return "admin/categories";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("form", new CategoryForm());
        return "admin/category-form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow();
        CategoryForm form = new CategoryForm();
        form.setId(category.getId());
        form.setName(category.getName());
        form.setSlug(category.getSlug());
        form.setDisplayOrder(category.getDisplayOrder());
        form.setFeatured(category.isFeatured());
        form.setActive(category.isActive());
        model.addAttribute("form", form);
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") CategoryForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/category-form";
        }
        Category category = form.getId() != null ? categoryRepository.findById(form.getId()).orElse(new Category()) : new Category();
        category.setName(form.getName());
        category.setSlug(form.getSlug());
        category.setDisplayOrder(form.getDisplayOrder());
        category.setFeatured(form.isFeatured());
        category.setActive(form.isActive());
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }
}
