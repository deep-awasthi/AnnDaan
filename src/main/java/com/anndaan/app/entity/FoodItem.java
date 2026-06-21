package com.anndaan.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private User restaurant;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FoodItem() {
    }

    public FoodItem(UUID id, User restaurant, String title, String description, BigDecimal originalPrice, BigDecimal discountedPrice, Integer quantity, LocalDateTime expiryTime, String pickupLocation, Double latitude, Double longitude, FoodStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.restaurant = restaurant;
        this.title = title;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.quantity = quantity;
        this.expiryTime = expiryTime;
        this.pickupLocation = pickupLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FoodStatus.AVAILABLE;
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(User restaurant) {
        this.restaurant = restaurant;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public FoodStatus getStatus() {
        return status;
    }

    public void setStatus(FoodStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder Pattern
    public static FoodItemBuilder builder() {
        return new FoodItemBuilder();
    }

    public static class FoodItemBuilder {
        private UUID id;
        private User restaurant;
        private String title;
        private String description;
        private BigDecimal originalPrice;
        private BigDecimal discountedPrice;
        private Integer quantity;
        private LocalDateTime expiryTime;
        private String pickupLocation;
        private Double latitude;
        private Double longitude;
        private FoodStatus status;
        private LocalDateTime createdAt;

        public FoodItemBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public FoodItemBuilder restaurant(User restaurant) {
            this.restaurant = restaurant;
            return this;
        }

        public FoodItemBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FoodItemBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FoodItemBuilder originalPrice(BigDecimal originalPrice) {
            this.originalPrice = originalPrice;
            return this;
        }

        public FoodItemBuilder discountedPrice(BigDecimal discountedPrice) {
            this.discountedPrice = discountedPrice;
            return this;
        }

        public FoodItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public FoodItemBuilder expiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public FoodItemBuilder pickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public FoodItemBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public FoodItemBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public FoodItemBuilder status(FoodStatus status) {
            this.status = status;
            return this;
        }

        public FoodItemBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FoodItem build() {
            return new FoodItem(id, restaurant, title, description, originalPrice, discountedPrice, quantity, expiryTime, pickupLocation, latitude, longitude, status, createdAt);
        }
    }
}
