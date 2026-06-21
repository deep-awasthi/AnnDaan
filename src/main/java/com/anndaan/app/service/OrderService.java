package com.anndaan.app.service;

import com.anndaan.app.dto.OrderItemRequest;
import com.anndaan.app.dto.OrderItemResponse;
import com.anndaan.app.dto.OrderRequest;
import com.anndaan.app.dto.OrderResponse;
import com.anndaan.app.entity.*;
import com.anndaan.app.repository.FoodItemRepository;
import com.anndaan.app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final FoodItemRepository foodItemRepository;

    public OrderService(OrderRepository orderRepository, FoodItemRepository foodItemRepository) {
        this.orderRepository = orderRepository;
        this.foodItemRepository = foodItemRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, User customer) {
        if (request.items().isEmpty()) {
            throw new IllegalArgumentException("Cannot place an empty order");
        }

        // Validate items and verify restaurant matching
        User restaurant = null;
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
                .customer(customer)
                .deliveryAddress(request.deliveryAddress())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest itemReq : request.items()) {
            FoodItem foodItem = foodItemRepository.findById(itemReq.foodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Food item not found: " + itemReq.foodItemId()));

            if (foodItem.getStatus() != FoodStatus.AVAILABLE) {
                throw new IllegalStateException("Food item is no longer available: " + foodItem.getTitle());
            }

            if (foodItem.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Food item has expired: " + foodItem.getTitle());
            }

            if (foodItem.getQuantity() < itemReq.quantity()) {
                throw new IllegalStateException("Insufficient quantity for item: " + foodItem.getTitle() + 
                        ". Available: " + foodItem.getQuantity() + ", Requested: " + itemReq.quantity());
            }

            // All items must belong to the same restaurant
            if (restaurant == null) {
                restaurant = foodItem.getRestaurant();
            } else if (!restaurant.getId().equals(foodItem.getRestaurant().getId())) {
                throw new IllegalArgumentException("All order items must belong to the same restaurant");
            }

            // Deduct quantity
            int remainingQty = foodItem.getQuantity() - itemReq.quantity();
            foodItem.setQuantity(remainingQty);
            if (remainingQty == 0) {
                foodItem.setStatus(FoodStatus.SOLD_OUT);
            }
            foodItemRepository.save(foodItem);

            // Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .foodItem(foodItem)
                    .quantity(itemReq.quantity())
                    .priceAtPurchase(foodItem.getDiscountedPrice())
                    .build();

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(foodItem.getDiscountedPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
        }

        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Transactional
    public OrderResponse payOrder(UUID orderId, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to pay for this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order cannot be paid. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PAID);
        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Only customer who placed it or restaurant can cancel it
        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isRestaurant = order.getRestaurant().getId().equals(user.getId());

        if (!isCustomer && !isRestaurant) {
            throw new IllegalStateException("You are not authorized to cancel this order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be cancelled. Current status: " + order.getStatus());
        }

        // Return food item stock
        for (OrderItem item : order.getOrderItems()) {
            FoodItem foodItem = item.getFoodItem();
            foodItem.setQuantity(foodItem.getQuantity() + item.getQuantity());
            if (foodItem.getStatus() == FoodStatus.SOLD_OUT) {
                foodItem.setStatus(FoodStatus.AVAILABLE);
            }
            foodItemRepository.save(foodItem);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    public List<OrderResponse> getCustomerOrders(User customer) {
        return orderRepository.findByCustomer(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getRestaurantOrders(User restaurant) {
        return orderRepository.findByRestaurant(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(UUID id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Verify access (customer, restaurant, or a rider)
        boolean hasAccess = order.getCustomer().getId().equals(user.getId()) ||
                order.getRestaurant().getId().equals(user.getId()) ||
                user.getRole() == Role.RIDER;

        if (!hasAccess) {
            throw new IllegalStateException("You do not have access to view this order");
        }

        return mapToResponse(order);
    }

    public OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getFoodItem().getId(),
                        item.getFoodItem().getTitle(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getDeliveryAddress(),
                order.getLatitude(),
                order.getLongitude(),
                order.getTotalAmount(),
                order.getStatus(),
                items,
                order.getCreatedAt()
        );
    }
}
