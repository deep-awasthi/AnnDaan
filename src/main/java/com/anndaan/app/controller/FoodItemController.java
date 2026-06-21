package com.anndaan.app.controller;

import com.anndaan.app.dto.FoodItemRequest;
import com.anndaan.app.dto.FoodItemResponse;
import com.anndaan.app.entity.User;
import com.anndaan.app.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @Valid @RequestBody FoodItemRequest request,
            @AuthenticationPrincipal User restaurant
    ) {
        FoodItemResponse response = foodItemService.createFoodItem(request, restaurant);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable UUID id,
            @Valid @RequestBody FoodItemRequest request,
            @AuthenticationPrincipal User restaurant
    ) {
        FoodItemResponse response = foodItemService.updateFoodItem(id, request, restaurant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FoodItemResponse> expireFoodItem(
            @PathVariable UUID id,
            @AuthenticationPrincipal User restaurant
    ) {
        FoodItemResponse response = foodItemService.deleteOrExpireFoodItem(id, restaurant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant")
    public ResponseEntity<List<FoodItemResponse>> getRestaurantFoodItems(
            @AuthenticationPrincipal User restaurant
    ) {
        List<FoodItemResponse> responses = foodItemService.getRestaurantFoodItems(restaurant);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> responses = foodItemService.getAvailableFoodItems();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable UUID id) {
        FoodItemResponse response = foodItemService.getFoodItemById(id);
        return ResponseEntity.ok(response);
    }
}
