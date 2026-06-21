package com.anndaan.app.dto;

import com.anndaan.app.entity.FoodStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FoodItemResponse(
    UUID id,
    UUID restaurantId,
    String restaurantName,
    String title,
    String description,
    BigDecimal originalPrice,
    BigDecimal discountedPrice,
    Integer quantity,
    LocalDateTime expiryTime,
    String pickupLocation,
    Double latitude,
    Double longitude,
    FoodStatus status,
    LocalDateTime createdAt
) {}
