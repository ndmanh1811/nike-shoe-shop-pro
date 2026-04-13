package com.nikestore.shoeshop.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class CouponService {

    private final Map<String, CouponDefinition> coupons = new LinkedHashMap<>();

    public CouponService() {
        coupons.put("NIKE10", new CouponDefinition("NIKE10", "10% off", "10% discount up to 250,000đ for orders above 1,000,000đ", Type.PERCENT, 10d, 250000d, 1000000d));
        coupons.put("STREET15", new CouponDefinition("STREET15", "15% off", "15% discount up to 500,000đ for orders above 2,000,000đ", Type.PERCENT, 15d, 500000d, 2000000d));
        coupons.put("SNK100", new CouponDefinition("SNK100", "100,000đ off", "Flat 100,000đ discount for orders above 1,000,000đ", Type.FIXED, 100000d, 100000d, 1000000d));
        coupons.put("VIP20", new CouponDefinition("VIP20", "20% off", "20% discount up to 800,000đ for orders above 4,000,000đ", Type.PERCENT, 20d, 800000d, 4000000d));
    }

    public Optional<CouponQuote> quote(String rawCode, double subtotal) {
        if (rawCode == null || rawCode.isBlank()) {
            return Optional.empty();
        }
        String code = rawCode.trim().toUpperCase(Locale.ROOT);
        CouponDefinition def = coupons.get(code);
        if (def == null || subtotal < def.minAmount) {
            return Optional.empty();
        }
        double discount = def.type == Type.PERCENT ? subtotal * (def.value / 100d) : def.value;
        discount = Math.min(discount, def.maxDiscount);
        discount = Math.min(discount, subtotal);
        return Optional.of(new CouponQuote(def.code, def.title, def.description, discount));
    }

    public Map<String, CouponDefinition> coupons() {
        return coupons;
    }

    public static class CouponQuote {
        private final String code;
        private final String title;
        private final String description;
        private final double discountAmount;

        public CouponQuote(String code, String title, String description, double discountAmount) {
            this.code = code;
            this.title = title;
            this.description = description;
            this.discountAmount = discountAmount;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }
    }

    public static class CouponDefinition {
        private final String code;
        private final String title;
        private final String description;
        private final Type type;
        private final double value;
        private final double maxDiscount;
        private final double minAmount;

        public CouponDefinition(String code, String title, String description, Type type, double value, double maxDiscount, double minAmount) {
            this.code = code;
            this.title = title;
            this.description = description;
            this.type = type;
            this.value = value;
            this.maxDiscount = maxDiscount;
            this.minAmount = minAmount;
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Type getType() { return type; }
        public double getValue() { return value; }
        public double getMaxDiscount() { return maxDiscount; }
        public double getMinAmount() { return minAmount; }
    }

    public enum Type {
        PERCENT,
        FIXED
    }
}
