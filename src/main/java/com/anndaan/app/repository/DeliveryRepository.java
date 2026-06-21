package com.anndaan.app.repository;

import com.anndaan.app.entity.Delivery;
import com.anndaan.app.entity.Order;
import com.anndaan.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    List<Delivery> findByRider(User rider);
    Optional<Delivery> findByOrder(Order order);
}
