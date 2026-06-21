package com.anndaan.app.repository;

import com.anndaan.app.entity.Order;
import com.anndaan.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomer(User customer);
    List<Order> findByRestaurant(User restaurant);
}
