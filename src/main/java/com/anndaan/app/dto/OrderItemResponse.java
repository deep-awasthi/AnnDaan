package com.anndaan.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID foodItemId,
    String foodItemTitle,
    Integer quantity,
    BigDecimal priceAtPurchase
) {}
