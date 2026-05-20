package com.ecommerce.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderEventProducer {

    @Channel("order-placed-out")
    Emitter<String> emitter;

    public void sendOrderEvent(String message) {
        emitter.send(message);
        System.out.println("[KAFKA PRODUCER] Sent: " + message);
    }
}
