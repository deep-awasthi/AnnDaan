package com.anndaan.app.repository;

import com.anndaan.app.entity.FoodItem;
import com.anndaan.app.entity.FoodStatus;
import com.anndaan.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, UUID> {
    List<FoodItem> findByRestaurant(User restaurant);

    @Query("SELECT f FROM FoodItem f WHERE f.status = :status AND f.quantity > 0 AND f.expiryTime > :now")
    List<FoodItem> findActiveFoodItems(@Param("status") FoodStatus status, @Param("now") LocalDateTime now);
}
