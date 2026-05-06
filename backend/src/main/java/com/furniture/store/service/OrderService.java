package com.furniture.store.service;

import com.furniture.store.dto.order.CheckoutSessionResponse;
import com.furniture.store.dto.order.CreateCheckoutSessionRequest;
import com.furniture.store.dto.order.OrderItemResponse;
import com.furniture.store.dto.order.OrderResponse;
import com.furniture.store.exception.InsufficientStockException;
import com.furniture.store.exception.ResourceNotFoundException;
import com.furniture.store.model.*;
import com.furniture.store.repository.UserRepository;
import com.furniture.store.model.enums.OrderStatus;
import com.furniture.store.repository.OrderRepository;
import com.furniture.store.repository.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final StripeService stripeService;

    @Transactional
    public CheckoutSessionResponse createCheckoutSession(String email,
                                                          CreateCheckoutSessionRequest request) {
        Cart cart = cartService.getOrCreateCart(email);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Validate stock before creating order
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        product.getName(), item.getQuantity(), product.getStockQuantity());
            }
        }

        // Calculate total
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .totalAmount(total)
                .shippingAddress(request.getShippingAddress())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItems.add(orderItem);

            lineItems.add(stripeService.buildLineItem(
                    product.getName(), product.getPrice(), cartItem.getQuantity()));
        }

        order.setItems(orderItems);
        order = orderRepository.save(order);

        // Create Stripe checkout session
        try {
            Session session = stripeService.createCheckoutSession(
                    lineItems, cart.getUser().getEmail(), order.getId());
            order.setStripeSessionId(session.getId());
            orderRepository.save(order);
            return new CheckoutSessionResponse(session.getUrl(), order.getId());
        } catch (StripeException e) {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new RuntimeException("Payment initialization failed: " + e.getMessage());
        }
    }

    public Page<OrderResponse> getUserOrders(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }

    public OrderResponse getUserOrder(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if (!order.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Order", orderId);
        }
        return toResponse(order);
    }

    public OrderResponse getUserOrderBySession(String email, String sessionId) {
        Order order = orderRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for session"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Order not found for session");
        }
        return toResponse(order);
    }

    public Page<OrderResponse> getAllOrders(OrderStatus status, String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return orderRepository.findAllNoSearch(status, pageable).map(this::toResponse);
        }
        return orderRepository.findAllWithSearch(status, search, pageable).map(this::toResponse);
    }

    public OrderResponse getOrderById(Long id) {
        return toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id)));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    public Page<OrderResponse> getUserOrdersByEmail(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }

    private OrderResponse toResponse(Order order) {
        User user = order.getUser();
        return OrderResponse.builder()
                .id(order.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .stripeSessionId(order.getStripeSessionId())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(subtotal)
                .build();
    }
}
