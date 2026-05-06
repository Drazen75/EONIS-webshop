package com.furniture.store.repository;

import com.furniture.store.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByStripeSessionId(String stripeSessionId);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(:search IS NULL OR LOWER(t.customerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.stripeSessionId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
