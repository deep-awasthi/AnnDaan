package com.anndaan.app.controller;

import com.anndaan.app.dto.DeliveryResponse;
import com.anndaan.app.dto.LocationRequest;
import com.anndaan.app.dto.OrderResponse;
import com.anndaan.app.entity.User;
import com.anndaan.app.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<OrderResponse>> getAvailableOrdersForDelivery() {
        List<OrderResponse> responses = deliveryService.getAvailableOrdersForDelivery();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{orderId}/claim")
    public ResponseEntity<DeliveryResponse> claimDelivery(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal User rider
    ) {
        DeliveryResponse response = deliveryService.claimDelivery(orderId, rider);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<DeliveryResponse> updateLocation(
            @PathVariable UUID id,
            @Valid @RequestBody LocationRequest request,
            @AuthenticationPrincipal User rider
    ) {
        DeliveryResponse response = deliveryService.updateLocation(id, request.latitude(), request.longitude(), rider);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pickup")
    public ResponseEntity<DeliveryResponse> markPickedUp(
            @PathVariable UUID id,
            @AuthenticationPrincipal User rider
    ) {
        DeliveryResponse response = deliveryService.markPickedUp(id, rider);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<DeliveryResponse> markDelivered(
            @PathVariable UUID id,
            @AuthenticationPrincipal User rider
    ) {
        DeliveryResponse response = deliveryService.markDelivered(id, rider);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rider")
    public ResponseEntity<List<DeliveryResponse>> getRiderDeliveries(
            @AuthenticationPrincipal User rider
    ) {
        List<DeliveryResponse> responses = deliveryService.getRiderDeliveries(rider);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        DeliveryResponse response = deliveryService.getDeliveryById(id, user);
        return ResponseEntity.ok(response);
    }
}
