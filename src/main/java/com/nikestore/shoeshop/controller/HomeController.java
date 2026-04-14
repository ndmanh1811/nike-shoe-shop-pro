package com.nikestore.shoeshop.controller;

import com.nikestore.shoeshop.entity.Product;
import com.nikestore.shoeshop.service.CatalogService;
import com.nikestore.shoeshop.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final CatalogService catalogService;
    private final WishlistService wishlistService;

    public HomeController(CatalogService catalogService, WishlistService wishlistService) {
        this.catalogService = catalogService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("heroFeatured", catalogService.featuredProducts().stream().limit(3).toList());
        model.addAttribute("featuredProducts", catalogService.featuredProducts());
        model.addAttribute("bestsellers", catalogService.bestsellers());
        model.addAttribute("newestProducts", catalogService.newestProducts());
        model.addAttribute("featuredCategories", catalogService.categories().stream().filter(c -> c.isFeatured() && c.isActive()).toList());
        model.addAttribute("featuredBrands", catalogService.brands().stream().filter(b -> b.isFeatured() && b.isActive()).toList());
        return "shop/home";
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String shoeSize,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            Model model
    ) {
        List<Product> filtered = catalogService.filterProducts(keyword, categoryId, brandId, shoeSize, sort);

        int safePageSize = Math.max(4, Math.min(pageSize, 24));
        int totalItems = filtered.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) safePageSize));
        int currentPage = Math.max(1, Math.min(page, totalPages));

        int fromIndex = Math.min((currentPage - 1) * safePageSize, totalItems);
        int toIndex = Math.min(fromIndex + safePageSize, totalItems);
        List<Product> pageItems = filtered.subList(fromIndex, toIndex);

        model.addAttribute("products", pageItems);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("shoeSize", shoeSize);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", safePageSize);
        return "shop/products";
    }

    @GetMapping("/product/{slug}")
    public String detail(@PathVariable String slug, HttpSession session, Model model) {
        try {
            Product product = catalogService.requireProductBySlug(slug);
            logger.debug("Product loaded: {}", product.getName());
            model.addAttribute("product", product);
            model.addAttribute("availableSizes", catalogService.availableSizes(product));
            model.addAttribute("relatedProducts", catalogService.relatedProducts(product));
            model.addAttribute("inWishlist", wishlistService.contains(session, product.getId()));
            return "shop/product-detail";
        } catch (IllegalArgumentException e) {
            logger.warn("Product not found for slug: {}", slug);
            model.addAttribute("error", "Product not found");
            return "shop/product-detail";
        }
    }
}
