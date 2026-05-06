package com.furniture.store.controller;

import com.furniture.store.dto.order.CheckoutSessionResponse;
import com.furniture.store.dto.order.CreateCheckoutSessionRequest;
import com.furniture.store.dto.order.OrderResponse;
import com.furniture.store.model.enums.OrderStatus;
import com.furniture.store.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutSessionResponse> checkout(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateCheckoutSessionRequest request) {
        return ResponseEntity.ok(orderService.createCheckoutSession(user.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrdersByEmail(user.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getMyOrder(@AuthenticationPrincipal UserDetails user,
                                                     @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getUserOrder(user.getUsername(), id));
    }

    @GetMapping("/by-session")
    public ResponseEntity<OrderResponse> getBySession(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String sessionId) {
        return ResponseEntity.ok(orderService.getUserOrderBySession(user.getUsername(), sessionId));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(status, search, pageable));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                       @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
