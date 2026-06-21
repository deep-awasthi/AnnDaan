package com.anndaan.app.dto;

import com.anndaan.app.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID customerId,
    String customerName,
    UUID restaurantId,
    String restaurantName,
    String deliveryAddress,
    Double latitude,
    Double longitude,
    BigDecimal totalAmount,
    OrderStatus status,
    List<OrderItemResponse> items,
    LocalDateTime createdAt
) {}
