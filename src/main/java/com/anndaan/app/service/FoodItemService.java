package com.anndaan.app.service;

import com.anndaan.app.dto.FoodItemRequest;
import com.anndaan.app.dto.FoodItemResponse;
import com.anndaan.app.entity.FoodItem;
import com.anndaan.app.entity.FoodStatus;
import com.anndaan.app.entity.User;
import com.anndaan.app.repository.FoodItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Transactional
    public FoodItemResponse createFoodItem(FoodItemRequest request, User restaurant) {
        FoodItem foodItem = FoodItem.builder()
                .restaurant(restaurant)
                .title(request.title())
                .description(request.description())
                .originalPrice(request.originalPrice())
                .discountedPrice(request.discountedPrice())
                .quantity(request.quantity())
                .expiryTime(request.expiryTime())
                .pickupLocation(request.pickupLocation())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .status(FoodStatus.AVAILABLE)
                .build();

        FoodItem saved = foodItemRepository.save(foodItem);
        return mapToResponse(saved);
    }

    @Transactional
    public FoodItemResponse updateFoodItem(UUID id, FoodItemRequest request, User restaurant) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        if (!foodItem.getRestaurant().getId().equals(restaurant.getId())) {
            throw new IllegalStateException("You are not authorized to update this food item");
        }

        foodItem.setTitle(request.title());
        foodItem.setDescription(request.description());
        foodItem.setOriginalPrice(request.originalPrice());
        foodItem.setDiscountedPrice(request.discountedPrice());
        foodItem.setQuantity(request.quantity());
        foodItem.setExpiryTime(request.expiryTime());
        foodItem.setPickupLocation(request.pickupLocation());
        foodItem.setLatitude(request.latitude());
        foodItem.setLongitude(request.longitude());

        if (foodItem.getQuantity() <= 0) {
            foodItem.setStatus(FoodStatus.SOLD_OUT);
        } else if (foodItem.getExpiryTime().isBefore(LocalDateTime.now())) {
            foodItem.setStatus(FoodStatus.EXPIRED);
        } else {
            foodItem.setStatus(FoodStatus.AVAILABLE);
        }

        FoodItem updated = foodItemRepository.save(foodItem);
        return mapToResponse(updated);
    }

    public List<FoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findActiveFoodItems(FoodStatus.AVAILABLE, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getRestaurantFoodItems(User restaurant) {
        return foodItemRepository.findByRestaurant(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FoodItemResponse deleteOrExpireFoodItem(UUID id, User restaurant) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        if (!foodItem.getRestaurant().getId().equals(restaurant.getId())) {
            throw new IllegalStateException("You are not authorized to modify this food item");
        }

        foodItem.setStatus(FoodStatus.EXPIRED);
        FoodItem saved = foodItemRepository.save(foodItem);
        return mapToResponse(saved);
    }

    public FoodItemResponse getFoodItemById(UUID id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
        return mapToResponse(foodItem);
    }

    public FoodItemResponse mapToResponse(FoodItem foodItem) {
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getRestaurant().getId(),
                foodItem.getRestaurant().getName(),
                foodItem.getTitle(),
                foodItem.getDescription(),
                foodItem.getOriginalPrice(),
                foodItem.getDiscountedPrice(),
                foodItem.getQuantity(),
                foodItem.getExpiryTime(),
                foodItem.getPickupLocation(),
                foodItem.getLatitude(),
                foodItem.getLongitude(),
                foodItem.getStatus(),
                foodItem.getCreatedAt()
        );
    }
}
