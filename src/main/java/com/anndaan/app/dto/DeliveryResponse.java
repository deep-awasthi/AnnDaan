package com.anndaan.app.dto;

import com.anndaan.app.entity.DeliveryStatus;
import com.anndaan.app.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryResponse(
    UUID id,
    UUID orderId,
    OrderStatus orderStatus,
    String restaurantName,
    String pickupAddress,
    String deliveryAddress,
    UUID riderId,
    String riderName,
    DeliveryStatus status,
    Double currentLatitude,
    Double currentLongitude,
    LocalDateTime pickupTime,
    LocalDateTime deliveryTime,
    BigDecimal deliveryFee
) {}
