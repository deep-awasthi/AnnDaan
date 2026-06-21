package com.anndaan.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FoodItemRequest(
    @NotBlank(message = "Title cannot be blank")
    String title,

    String description,

    @NotNull(message = "Original price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Original price must be greater than zero")
    BigDecimal originalPrice,

    @NotNull(message = "Discounted price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discounted price must be greater than or equal to zero")
    BigDecimal discountedPrice,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @NotNull(message = "Expiry time is required")
    LocalDateTime expiryTime,

    @NotBlank(message = "Pickup location cannot be blank")
    String pickupLocation,

    Double latitude,
    Double longitude
) {}
