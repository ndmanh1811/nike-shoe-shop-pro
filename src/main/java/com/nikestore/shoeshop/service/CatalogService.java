package com.nikestore.shoeshop.service;

import com.nikestore.shoeshop.entity.Brand;
import com.nikestore.shoeshop.entity.Category;
import com.nikestore.shoeshop.entity.Product;
import com.nikestore.shoeshop.repository.BrandRepository;
import com.nikestore.shoeshop.repository.CategoryRepository;
import com.nikestore.shoeshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public CatalogService(ProductRepository productRepository, CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public List<Category> categories() {
        return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc();
    }

    public List<Brand> brands() {
        return brandRepository.findAllByOrderByNameAsc();
    }

    public List<Product> featuredProducts() {
        return productRepository.findTop8ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> bestsellers() {
        return productRepository.findTop8ByBestsellerTrueAndActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> newestProducts() {
        return productRepository.findAllByActiveTrueOrderByCreatedAtDesc()
                .stream().limit(12).collect(Collectors.toList());
    }

    public List<Product> filterProducts(String keyword, Long categoryId, Long brandId, String shoeSize, String sort) {
        List<Product> list = productRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(p -> keyword == null || keyword.isBlank() ||
                        p.getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)) ||
                        (p.getBrand() != null && p.getBrand().getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) ||
                        (p.getCategory() != null && p.getCategory().getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))))
                .filter(p -> categoryId == null || (p.getCategory() != null && categoryId.equals(p.getCategory().getId())))
                .filter(p -> brandId == null || (p.getBrand() != null && brandId.equals(p.getBrand().getId())))
                .filter(p -> shoeSize == null || shoeSize.isBlank() || matchesSizeRange(p.getSizeRange(), shoeSize))
                .collect(Collectors.toList());

        Comparator<Product> byCreated = Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<Product> byPrice = Comparator.comparing(Product::effectivePrice);

        if ("price-asc".equals(sort)) {
            list.sort(byPrice);
        } else if ("price-desc".equals(sort)) {
            list.sort(byPrice.reversed());
        } else if ("featured".equals(sort)) {
            list.sort(Comparator.comparing(Product::isFeatured).reversed().thenComparing(byCreated.reversed()));
        } else if ("bestseller".equals(sort)) {
            list.sort(Comparator.comparing(Product::isBestseller).reversed().thenComparing(byCreated.reversed()));
        } else {
            list.sort(byCreated.reversed());
        }
        return list;
    }

    public Product requireProductBySlug(String slug) {
        return productRepository.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product requireProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> relatedProducts(Product product) {
        if (product.getCategory() == null && product.getBrand() == null) {
            return newestProducts().stream()
                    .filter(p -> !Objects.equals(p.getId(), product.getId()))
                    .limit(4)
                    .toList();
        }
        return productRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(p -> !Objects.equals(p.getId(), product.getId()))
                .filter(p -> (product.getCategory() != null && p.getCategory() != null && Objects.equals(product.getCategory().getId(), p.getCategory().getId()))
                        || (product.getBrand() != null && p.getBrand() != null && Objects.equals(product.getBrand().getId(), p.getBrand().getId())))
                .limit(4)
                .toList();
    }

    public List<String> availableSizes(Product product) {
        return expandSizes(product.getSizeRange());
    }

    public List<String> expandSizes(String sizeRange) {
        if (sizeRange == null || sizeRange.isBlank()) {
            return List.of("39", "40", "41", "42", "43", "44", "45");
        }
        String normalized = sizeRange.trim();
        if (normalized.contains("-")) {
            String[] parts = normalized.split("-");
            if (parts.length == 2) {
                try {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    List<String> sizes = new ArrayList<>();
                    for (int i = start; i <= end; i++) {
                        sizes.add(String.valueOf(i));
                    }
                    return sizes;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return Arrays.stream(normalized.split("[,;/\s]+"))
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }

    private boolean matchesSizeRange(String sizeRange, String selectedSize) {
        if (sizeRange == null || sizeRange.isBlank()) {
            return true;
        }
        if (selectedSize == null || selectedSize.isBlank()) {
            return true;
        }
        String normalized = sizeRange.trim();
        String wanted = selectedSize.trim();
        if (normalized.contains("-")) {
            String[] parts = normalized.split("-");
            if (parts.length == 2) {
                try {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    int size = Integer.parseInt(wanted);
                    return size >= start && size <= end;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return Arrays.stream(normalized.split("[,;/\s]+")).anyMatch(s -> s.trim().equalsIgnoreCase(wanted));
    }
}
