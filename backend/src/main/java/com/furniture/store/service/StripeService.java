package com.furniture.store.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Session createCheckoutSession(List<SessionCreateParams.LineItem> lineItems,
                                         String customerEmail,
                                         Long orderId) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .setCustomerEmail(customerEmail)
                .addAllLineItem(lineItems)
                .putMetadata("orderId", orderId.toString())
                .build();

        return Session.create(params);
    }

    public SessionCreateParams.LineItem buildLineItem(String productName,
                                                       BigDecimal price,
                                                       Integer quantity) {
        long unitAmountInCents = price.multiply(BigDecimal.valueOf(100)).longValue();

        return SessionCreateParams.LineItem.builder()
                .setQuantity(quantity.longValue())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("rsd")
                                .setUnitAmount(unitAmountInCents)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(productName)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
