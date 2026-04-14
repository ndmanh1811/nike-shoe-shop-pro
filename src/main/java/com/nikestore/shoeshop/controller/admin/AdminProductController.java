package com.nikestore.shoeshop.controller.admin;

import com.nikestore.shoeshop.dto.ProductForm;
import com.nikestore.shoeshop.entity.Brand;
import com.nikestore.shoeshop.entity.Category;
import com.nikestore.shoeshop.entity.Product;
import com.nikestore.shoeshop.repository.BrandRepository;
import com.nikestore.shoeshop.repository.CategoryRepository;
import com.nikestore.shoeshop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public AdminProductController(ProductRepository productRepository,
                                  CategoryRepository categoryRepository,
                                  BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    // 🔥 LIST + SEARCH
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("products",
                    productRepository.findByNameContainingIgnoreCase(keyword));
        } else {
            model.addAttribute("products",
                    productRepository.findAllByActiveTrueOrderByCreatedAtDesc());
        }

        model.addAttribute("keyword", keyword);

        return "admin/products";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("form", new ProductForm());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();

        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setSlug(product.getSlug());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setSalePrice(product.getSalePrice());
        form.setImageUrl(product.getImageUrl());
        form.setStock(product.getStock());
        form.setSizeRange(product.getSizeRange());
        form.setFeatured(product.isFeatured());
        form.setBestseller(product.isBestseller());
        form.setActive(product.isActive());
        form.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        form.setBrandId(product.getBrand() != null ? product.getBrand().getId() : null);

        model.addAttribute("form", form);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());

        return "admin/product-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") ProductForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "admin/product-form";
        }

        // Check for duplicate slug (exclude current product when editing)
        if (productRepository.existsBySlug(form.getSlug())) {
            Product existing = productRepository.findBySlug(form.getSlug()).orElse(null);
            if (existing != null && (form.getId() == null || !existing.getId().equals(form.getId()))) {
                bindingResult.rejectValue("slug", "duplicate", "Slug already exists");
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("brands", brandRepository.findAll());
                return "admin/product-form";
            }
        }

        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        Brand brand = brandRepository.findById(form.getBrandId()).orElseThrow();

        Product product = form.getId() != null
                ? productRepository.findById(form.getId()).orElse(new Product())
                : new Product();

        product.setName(form.getName());
        product.setSlug(form.getSlug());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setSalePrice(form.getSalePrice());
        product.setImageUrl(form.getImageUrl());
        product.setStock(form.getStock());
        product.setSizeRange(form.getSizeRange());
        product.setFeatured(form.isFeatured());
        product.setBestseller(form.isBestseller());
        product.setActive(form.isActive());
        product.setCategory(category);
        product.setBrand(brand);

        productRepository.save(product);

        // 🔥 TOAST
        redirectAttributes.addFlashAttribute("success", "Saved successfully!");

        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        return "redirect:/admin/products";
    }
}