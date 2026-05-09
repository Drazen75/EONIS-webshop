package com.furniture.store.controller;

import com.furniture.store.model.Transaction;
import com.furniture.store.model.enums.OrderStatus;
import com.furniture.store.repository.CartRepository;
import com.furniture.store.repository.OrderRepository;
import com.furniture.store.repository.TransactionRepository;
import com.furniture.store.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    @Transactional
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (session == null) {
                log.error("Could not deserialize Stripe session");
                return ResponseEntity.ok("Skipped");
            }

            String orderId = session.getMetadata().get("orderId");
            if (orderId == null) {
                log.error("No orderId in Stripe session metadata");
                return ResponseEntity.ok("Skipped");
            }

            orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);

                // Stock was already decremented at order creation time
                // (see OrderService.createCheckoutSession). Nothing to do here.

                // Clear cart — explicit save triggers orphanRemoval on items
                var cart = order.getUser().getCart();
                if (cart != null) {
                    cart.getItems().clear();
                    cartRepository.save(cart);
                }

                // Save transaction record
                if (!transactionRepository.findByStripeSessionId(session.getId()).isPresent()) {
                    BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal())
                            .divide(BigDecimal.valueOf(100));

                    Transaction transaction = Transaction.builder()
                            .order(order)
                            .stripeSessionId(session.getId())
                            .stripePaymentIntentId(session.getPaymentIntent())
                            .amount(amount)
                            .currency(session.getCurrency().toUpperCase())
                            .customerEmail(session.getCustomerEmail())
                            .status("COMPLETED")
                            .build();
                    transactionRepository.save(transaction);

                    log.info("Payment completed for order {} by {}, amount: {} {}",
                            orderId, session.getCustomerEmail(), amount, session.getCurrency());
                }
            });
        } else if ("checkout.session.expired".equals(event.getType())) {
            // User abandoned the Stripe page; Stripe expires the session
            // (default ~24h). Roll the order back so stock returns to inventory.
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (session != null) {
                orderService.expireBySessionId(session.getId());
                log.info("Stripe session {} expired — order cancelled, stock restored.",
                        session.getId());
            }
        }

        return ResponseEntity.ok("OK");
    }
}
