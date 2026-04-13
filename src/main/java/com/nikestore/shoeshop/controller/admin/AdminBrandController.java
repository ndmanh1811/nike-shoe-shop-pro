package com.nikestore.shoeshop.controller.admin;

import com.nikestore.shoeshop.dto.BrandForm;
import com.nikestore.shoeshop.entity.Brand;
import com.nikestore.shoeshop.repository.BrandRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    private final BrandRepository brandRepository;

    public AdminBrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("brands", brandRepository.findAllByOrderByNameAsc());
        return "admin/brands";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("form", new BrandForm());
        return "admin/brand-form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Brand brand = brandRepository.findById(id).orElseThrow();
        BrandForm form = new BrandForm();
        form.setId(brand.getId());
        form.setName(brand.getName());
        form.setLogoUrl(brand.getLogoUrl());
        form.setFeatured(brand.isFeatured());
        form.setActive(brand.isActive());
        model.addAttribute("form", form);
        return "admin/brand-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") BrandForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/brand-form";
        }
        Brand brand = form.getId() != null ? brandRepository.findById(form.getId()).orElse(new Brand()) : new Brand();
        brand.setName(form.getName());
        brand.setLogoUrl(form.getLogoUrl());
        brand.setFeatured(form.isFeatured());
        brand.setActive(form.isActive());
        brandRepository.save(brand);
        return "redirect:/admin/brands";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        brandRepository.deleteById(id);
        return "redirect:/admin/brands";
    }
}
