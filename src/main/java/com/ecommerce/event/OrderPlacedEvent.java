package com.ecommerce.event;

import java.math.BigDecimal;

public class OrderPlacedEvent {

    public Long orderId;
    public Long userId;
    public BigDecimal totalAmount;
    public String status;

}
