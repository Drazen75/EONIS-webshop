package com.furniture.store.controller;

import com.furniture.store.dto.transaction.TransactionResponse;
import com.furniture.store.dto.user.UpdateUserRequest;
import com.furniture.store.dto.user.UserResponse;
import com.furniture.store.model.Transaction;
import com.furniture.store.repository.TransactionRepository;
import com.furniture.store.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final TransactionRepository transactionRepository;

    // --- Users ---

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(search, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        userService.activate(id);
        return ResponseEntity.ok(userService.getById(id));
    }

    // --- Transactions ---

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(
                transactionRepository.findAllWithSearch(search, pageable)
                        .map(this::toTransactionResponse)
        );
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new com.furniture.store.exception.ResourceNotFoundException("Transaction", id));
        return ResponseEntity.ok(toTransactionResponse(t));
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        var order = t.getOrder();
        var user = order.getUser();
        return TransactionResponse.builder()
                .id(t.getId())
                .orderId(order.getId())
                .stripeSessionId(t.getStripeSessionId())
                .stripePaymentIntentId(t.getStripePaymentIntentId())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .customerEmail(t.getCustomerEmail())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .shippingAddress(order.getShippingAddress())
                .itemCount(order.getItems().size())
                .build();
    }
}
