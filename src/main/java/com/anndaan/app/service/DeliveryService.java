package com.anndaan.app.service;

import com.anndaan.app.dto.DeliveryResponse;
import com.anndaan.app.dto.OrderResponse;
import com.anndaan.app.entity.*;
import com.anndaan.app.repository.DeliveryRepository;
import com.anndaan.app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository, OrderService orderService) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAvailableOrdersForDelivery() {
        // Find orders that are PAID and do not have a delivery record
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .filter(order -> deliveryRepository.findByOrder(order).isEmpty())
                .map(orderService::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryResponse claimDelivery(UUID orderId, User rider) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Order is not in PAID state and cannot be claimed. Status: " + order.getStatus());
        }

        if (deliveryRepository.findByOrder(order).isPresent()) {
            throw new IllegalStateException("Delivery has already been claimed by another rider");
        }

        // Flat delivery fee of 3.50 for cheap leftover delivery
        BigDecimal deliveryFee = BigDecimal.valueOf(3.50);

        order.setStatus(OrderStatus.ACCEPTED_BY_RIDER);
        orderRepository.save(order);

        Delivery delivery = Delivery.builder()
                .order(order)
                .rider(rider)
                .status(DeliveryStatus.ASSIGNED)
                .currentLatitude(rider.getLatitude())
                .currentLongitude(rider.getLongitude())
                .deliveryFee(deliveryFee)
                .build();

        Delivery saved = deliveryRepository.save(delivery);
        return mapToResponse(saved);
    }

    @Transactional
    public DeliveryResponse updateLocation(UUID id, Double latitude, Double longitude, User rider) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (!delivery.getRider().getId().equals(rider.getId())) {
            throw new IllegalStateException("You are not authorized to update this delivery location");
        }

        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);
        Delivery saved = deliveryRepository.save(delivery);
        return mapToResponse(saved);
    }

    @Transactional
    public DeliveryResponse markPickedUp(UUID id, User rider) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (!delivery.getRider().getId().equals(rider.getId())) {
            throw new IllegalStateException("You are not authorized to update this delivery");
        }

        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Delivery cannot be marked picked up. Current status: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickupTime(LocalDateTime.now());
        
        Order order = delivery.getOrder();
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);

        Delivery saved = deliveryRepository.save(delivery);
        return mapToResponse(saved);
    }

    @Transactional
    public DeliveryResponse markDelivered(UUID id, User rider) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (!delivery.getRider().getId().equals(rider.getId())) {
            throw new IllegalStateException("You are not authorized to update this delivery");
        }

        if (delivery.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException("Delivery cannot be marked delivered. Current status: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveryTime(LocalDateTime.now());

        Order order = delivery.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        Delivery saved = deliveryRepository.save(delivery);
        return mapToResponse(saved);
    }

    public List<DeliveryResponse> getRiderDeliveries(User rider) {
        return deliveryRepository.findByRider(rider)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryResponse getDeliveryById(UUID id, User user) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        // Verify access (rider, customer, or restaurant)
        boolean hasAccess = delivery.getRider().getId().equals(user.getId()) ||
                delivery.getOrder().getCustomer().getId().equals(user.getId()) ||
                delivery.getOrder().getRestaurant().getId().equals(user.getId());

        if (!hasAccess) {
            throw new IllegalStateException("You do not have access to view this delivery tracking");
        }

        return mapToResponse(delivery);
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrder().getId(),
                delivery.getOrder().getStatus(),
                delivery.getOrder().getRestaurant().getName(),
                delivery.getOrder().getRestaurant().getAddress(),
                delivery.getOrder().getDeliveryAddress(),
                delivery.getRider().getId(),
                delivery.getRider().getName(),
                delivery.getStatus(),
                delivery.getCurrentLatitude(),
                delivery.getCurrentLongitude(),
                delivery.getPickupTime(),
                delivery.getDeliveryTime(),
                delivery.getDeliveryFee()
        );
    }
}
