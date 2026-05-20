package com.ecommerce.dto;

import java.util.List;

public class PlaceOrderInput {

    public Long userId;

    public List<OrderItemInput> items;

    public static class OrderItemInput {
        public Long productId;
        public int quantity;
    }
}
