package com.anndaan.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "delivery_fee")
    private BigDecimal deliveryFee;

    public Delivery() {
    }

    public Delivery(UUID id, Order order, User rider, DeliveryStatus status, Double currentLatitude, Double currentLongitude, LocalDateTime pickupTime, LocalDateTime deliveryTime, BigDecimal deliveryFee) {
        this.id = id;
        this.order = order;
        this.rider = rider;
        this.status = status;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
        this.deliveryFee = deliveryFee;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getRider() {
        return rider;
    }

    public void setRider(User rider) {
        this.rider = rider;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    // Builder Pattern
    public static DeliveryBuilder builder() {
        return new DeliveryBuilder();
    }

    public static class DeliveryBuilder {
        private UUID id;
        private Order order;
        private User rider;
        private DeliveryStatus status;
        private Double currentLatitude;
        private Double currentLongitude;
        private LocalDateTime pickupTime;
        private LocalDateTime deliveryTime;
        private BigDecimal deliveryFee;

        public DeliveryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DeliveryBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public DeliveryBuilder rider(User rider) {
            this.rider = rider;
            return this;
        }

        public DeliveryBuilder status(DeliveryStatus status) {
            this.status = status;
            return this;
        }

        public DeliveryBuilder currentLatitude(Double currentLatitude) {
            this.currentLatitude = currentLatitude;
            return this;
        }

        public DeliveryBuilder currentLongitude(Double currentLongitude) {
            this.currentLongitude = currentLongitude;
            return this;
        }

        public DeliveryBuilder pickupTime(LocalDateTime pickupTime) {
            this.pickupTime = pickupTime;
            return this;
        }

        public DeliveryBuilder deliveryTime(LocalDateTime deliveryTime) {
            this.deliveryTime = deliveryTime;
            return this;
        }

        public DeliveryBuilder deliveryFee(BigDecimal deliveryFee) {
            this.deliveryFee = deliveryFee;
            return this;
        }

        public Delivery build() {
            return new Delivery(id, order, rider, status, currentLatitude, currentLongitude, pickupTime, deliveryTime, deliveryFee);
        }
    }
}
