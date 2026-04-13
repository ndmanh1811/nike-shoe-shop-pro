package com.nikestore.shoeshop.config;

import com.nikestore.shoeshop.entity.*;
import com.nikestore.shoeshop.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            AppUserRepository userRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProductRepository productRepository,
            CustomerOrderRepository orderRepository,
            OrderStatusHistoryRepository statusHistoryRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Only seed if database is completely empty
            if (userRepository.count() > 0 || categoryRepository.count() > 0 || brandRepository.count() > 0) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setEmail("admin@gmail.com");
            admin.setFullName("Nike Admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(AppRole.ROLE_ADMIN);
            admin.setEnabled(true);
            admin.setPhone("0900000001");
            admin.setAddress("Hanoi");
            userRepository.save(admin);

            AppUser user = new AppUser();
            user.setEmail("user@gmail.com");
            user.setFullName("Sneaker User");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(AppRole.ROLE_USER);
            user.setEnabled(true);
            user.setPhone("0900000002");
            user.setAddress("Ho Chi Minh City");
            userRepository.save(user);

            Category running = createCategory("Running", "running", 1, true, true);
            Category lifestyle = createCategory("Lifestyle", "lifestyle", 2, true, true);
            Category basketball = createCategory("Basketball", "basketball", 3, true, true);
            Category training = createCategory("Training", "training", 4, false, true);
            Category retro = createCategory("Retro", "retro", 5, false, true);
            categoryRepository.save(running);
            categoryRepository.save(lifestyle);
            categoryRepository.save(basketball);
            categoryRepository.save(training);
            categoryRepository.save(retro);

            Brand nike = createBrand("Nike", "/img/logo/nike-logo.svg", true, true);
            Brand jordan = createBrand("Jordan", "/img/logo/jordan-logo.svg", true, true);
            Brand adidas = createBrand("Adidas", "/img/logo/adidas-logo.svg", true, true);
            Brand puma = createBrand("Puma", "/img/logo/puma-logo.svg", false, true);
            Brand newBalance = createBrand("New Balance", "/img/logo/nb-logo.svg", false, true);
            brandRepository.save(nike);
            brandRepository.save(jordan);
            brandRepository.save(adidas);
            brandRepository.save(puma);
            brandRepository.save(newBalance);

            productRepository.save(createProduct("Air Zoom Pegasus 41", "air-zoom-pegasus-41",
                    "Lightweight runner built for daily miles with a snappy ride and cushioned feel.",
                    4290000d, 3790000d, "/img/products/pegasus.svg", 26, "39-45", true, true, true, running, nike));
            productRepository.save(createProduct("Air Force 1 '07", "air-force-1-07",
                    "Classic silhouette with clean leather and street-ready comfort.",
                    3990000d, 3590000d, "/img/products/air-force.svg", 34, "39-45", true, true, true, lifestyle, nike));
            productRepository.save(createProduct("Jordan 1 Low", "jordan-1-low",
                    "Iconic court style with modern comfort and premium materials.",
                    4890000d, 4490000d, "/img/products/jordan-1.svg", 18, "40-46", true, true, true, basketball, jordan));
            productRepository.save(createProduct("Adidas Ultraboost 5", "adidas-ultraboost-5",
                    "Energy-return foam and premium knit upper for all-day wear.",
                    5290000d, 4790000d, "/img/products/ultraboost.svg", 22, "39-45", true, false, true, running, adidas));
            productRepository.save(createProduct("Puma RS-X³", "puma-rsx3",
                    "Chunky lifestyle look with vivid color and all-day cushioning.",
                    3490000d, 3090000d, "/img/products/puma-rsx.svg", 20, "39-45", false, true, true, lifestyle, puma));
            productRepository.save(createProduct("New Balance 550", "new-balance-550",
                    "Retro basketball icon with premium leather finish.",
                    4590000d, 4190000d, "/img/products/nb550.svg", 12, "39-45", true, false, true, retro, newBalance));
            productRepository.save(createProduct("Nike Free Metcon 5", "nike-free-metcon-5",
                    "Built for training sessions with a stable base and flexible forefoot.",
                    4090000d, 3690000d, "/img/products/metcon.svg", 15, "39-45", false, true, true, training, nike));
            productRepository.save(createProduct("Jordan Tatum 2", "jordan-tatum-2",
                    "Court performance and explosive style for modern basketball.",
                    5590000d, 4990000d, "/img/products/tatum2.svg", 10, "40-46", true, true, true, basketball, jordan));
            productRepository.save(createProduct("Adidas Samba OG", "adidas-samba-og",
                    "Timeless streetwear icon with a slim profile and gum sole.",
                    2990000d, 2690000d, "/img/products/samba.svg", 30, "39-45", true, false, true, lifestyle, adidas));
            productRepository.save(createProduct("Nike Zoom Fly 5", "nike-zoom-fly-5",
                    "Fast-feeling racing trainer made for tempo days.",
                    5190000d, 4590000d, "/img/products/zoom-fly.svg", 14, "39-45", false, true, true, running, nike));

            if (orderRepository.count() == 0) {
                CustomerOrder o1 = new CustomerOrder();
                o1.setOrderCode("NK-DEMO-01");
                o1.setCustomerName("Sneaker User");
                o1.setEmail("user@gmail.com");
                o1.setPhone("0900000002");
                o1.setAddress("Ho Chi Minh City");
                o1.setPaymentMethod(PaymentMethod.COD);
                o1.setStatus(OrderStatus.COMPLETED);
                o1.setSubtotalAmount(7380000d);
                o1.setDiscountAmount(0d);
                o1.setTotalAmount(7380000d);
                CustomerOrderItem i1 = new CustomerOrderItem();
                i1.setProductName("Air Force 1 '07");
                i1.setProductImage("/img/products/air-force.svg");
                i1.setUnitPrice(3590000d);
                i1.setQuantity(1);
                i1.setSize("42");
                i1.setSubtotal(3590000d);
                o1.addItem(i1);
                CustomerOrderItem i2 = new CustomerOrderItem();
                i2.setProductName("Adidas Samba OG");
                i2.setProductImage("/img/products/samba.svg");
                i2.setUnitPrice(2690000d);
                i2.setQuantity(1);
                i2.setSize("41");
                i2.setSubtotal(2690000d);
                o1.addItem(i2);
                orderRepository.save(o1);
                // Create status history for o1
                statusHistoryRepository.save(new OrderStatusHistory(o1, OrderStatus.PENDING, "Order placed, cash on delivery"));
                statusHistoryRepository.save(new OrderStatusHistory(o1, OrderStatus.PAID, "Payment confirmed on delivery"));
                statusHistoryRepository.save(new OrderStatusHistory(o1, OrderStatus.COMPLETED, "Order completed successfully"));

                CustomerOrder o2 = new CustomerOrder();
                o2.setOrderCode("NK-DEMO-02");
                o2.setCustomerName("Nike Admin");
                o2.setEmail("admin@gmail.com");
                o2.setPhone("0900000001");
                o2.setAddress("Hanoi");
                o2.setPaymentMethod(PaymentMethod.ONLINE);
                o2.setStatus(OrderStatus.PAID);
                o2.setSubtotalAmount(4990000d);
                o2.setDiscountAmount(0d);
                o2.setTotalAmount(4990000d);
                CustomerOrderItem i3 = new CustomerOrderItem();
                i3.setProductName("Jordan Tatum 2");
                i3.setProductImage("/img/products/tatum2.svg");
                i3.setUnitPrice(4990000d);
                i3.setQuantity(1);
                i3.setSize("43");
                i3.setSubtotal(4990000d);
                o2.addItem(i3);
                orderRepository.save(o2);
                // Create status history for o2
                statusHistoryRepository.save(new OrderStatusHistory(o2, OrderStatus.PENDING, "Order placed, awaiting online payment"));
                statusHistoryRepository.save(new OrderStatusHistory(o2, OrderStatus.PAID, "Online payment received"));
            }
        };
    }

    private Category createCategory(String name, String slug, Integer displayOrder, boolean featured, boolean active) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDisplayOrder(displayOrder);
        c.setFeatured(featured);
        c.setActive(active);
        return c;
    }

    private Brand createBrand(String name, String logoUrl, boolean featured, boolean active) {
        Brand b = new Brand();
        b.setName(name);
        b.setLogoUrl(logoUrl);
        b.setFeatured(featured);
        b.setActive(active);
        return b;
    }

    private Product createProduct(
            String name, String slug, String description, Double price, Double salePrice,
            String imageUrl, Integer stock, String sizeRange, boolean featured, boolean bestseller, boolean active,
            Category category, Brand brand
    ) {
        Product p = new Product();
        p.setName(name);
        p.setSlug(slug);
        p.setDescription(description);
        p.setPrice(price);
        p.setSalePrice(salePrice);
        p.setImageUrl(imageUrl);
        p.setStock(stock);
        p.setSizeRange(sizeRange);
        p.setFeatured(featured);
        p.setBestseller(bestseller);
        p.setActive(active);
        p.setCategory(category);
        p.setBrand(brand);
        return p;
    }
}
