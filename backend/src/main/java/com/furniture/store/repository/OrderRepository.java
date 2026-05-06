package com.furniture.store.repository;

import com.furniture.store.model.Order;
import com.furniture.store.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByStripeSessionId(String stripeSessionId);

    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status)")
    Page<Order> findAllNoSearch(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(o.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Order> findAllWithSearch(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable);

    List<Order> findByUserId(Long userId);
}
