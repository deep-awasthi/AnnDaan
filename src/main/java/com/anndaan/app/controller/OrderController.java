package com.anndaan.app.controller;

import com.anndaan.app.dto.OrderRequest;
import com.anndaan.app.dto.OrderResponse;
import com.anndaan.app.entity.User;
import com.anndaan.app.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User customer
    ) {
        OrderResponse response = orderService.createOrder(request, customer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal User customer
    ) {
        OrderResponse response = orderService.payOrder(id, customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        OrderResponse response = orderService.cancelOrder(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @AuthenticationPrincipal User customer
    ) {
        List<OrderResponse> responses = orderService.getCustomerOrders(customer);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/restaurant")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @AuthenticationPrincipal User restaurant
    ) {
        List<OrderResponse> responses = orderService.getRestaurantOrders(restaurant);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        OrderResponse response = orderService.getOrderById(id, user);
        return ResponseEntity.ok(response);
    }
}
