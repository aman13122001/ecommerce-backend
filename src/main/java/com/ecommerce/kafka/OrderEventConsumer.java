package com.ecommerce.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class OrderEventConsumer {

    @Incoming("order-placed-in")
    public void consume(String message) {
        System.out.println("[KAFKA CONSUMER] Received: " + message);
        if (message.startsWith("ORDER_PLACED"))
            System.out.println("[KAFKA] New order: " + message);
        else if (message.startsWith("ORDER_CANCELLED"))
            System.out.println("[KAFKA] Cancelled: " + message);
        else if (message.startsWith("ORDER_STATUS_UPDATED"))
            System.out.println("[KAFKA] Status update: " + message);
    }
}
