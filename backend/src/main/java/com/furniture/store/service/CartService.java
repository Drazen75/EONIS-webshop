package com.furniture.store.service;

import com.furniture.store.dto.cart.AddToCartRequest;
import com.furniture.store.dto.cart.CartItemResponse;
import com.furniture.store.dto.cart.CartResponse;
import com.furniture.store.dto.cart.UpdateCartItemRequest;
import com.furniture.store.exception.InsufficientStockException;
import com.furniture.store.exception.ResourceNotFoundException;
import com.furniture.store.model.Cart;
import com.furniture.store.model.CartItem;
import com.furniture.store.model.Product;
import com.furniture.store.model.User;
import com.furniture.store.repository.CartItemRepository;
import com.furniture.store.repository.CartRepository;
import com.furniture.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddToCartRequest request) {
        Cart cart = getOrCreateCart(email);
        Product product = productService.findById(request.getProductId());

        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product", request.getProductId());
        }

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        int newQuantity = request.getQuantity();
        if (existing.isPresent()) {
            newQuantity += existing.get().getQuantity();
        }

        // Business rule: cannot add more than available stock
        if (newQuantity > product.getStockQuantity()) {
            throw new InsufficientStockException(product.getName(), newQuantity, product.getStockQuantity());
        }

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse updateItem(String email, Long productId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(email);

        // Note: stock validation intentionally removed here.
        // Cart is just a wishlist — stock is verified again at checkout time
        // (see OrderService.createCheckoutSession), and decremented only after
        // successful Stripe payment (see WebhookController).

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse removeItem(String email, Long productId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    public CartResponse toResponse(Cart cart) {
        var items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalAmount(total)
                .totalItems(totalItems)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .productPrice(item.getProduct().getPrice())
                .stockQuantity(item.getProduct().getStockQuantity())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
