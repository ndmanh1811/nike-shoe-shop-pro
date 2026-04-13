package com.nikestore.shoeshop.dto;

public class CartItemView {
    private Long productId;
    private String name;
    private String slug;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private String size;
    private Double subtotal;

    public CartItemView() {}

    public CartItemView(Long productId, String name, String slug, String imageUrl, Double price, Integer quantity, String size) {
        this.productId = productId;
        this.name = name;
        this.slug = slug;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.subtotal = price * quantity;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getImageUrl() { return imageUrl; }
    public Double getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getSize() { return size; }
    public Double getSubtotal() { return subtotal; }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalc();
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void recalc() {
        this.subtotal = this.price * this.quantity;
    }
}
