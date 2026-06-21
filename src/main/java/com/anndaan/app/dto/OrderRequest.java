package com.anndaan.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    List<OrderItemRequest> items,

    @NotBlank(message = "Delivery address is required")
    String deliveryAddress,

    Double latitude,
    Double longitude
) {}
