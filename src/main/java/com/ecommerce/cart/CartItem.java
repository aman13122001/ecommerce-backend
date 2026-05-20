package com.ecommerce.cart;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {

    public Long productId;
    public String productName;
    public BigDecimal price;
    public int quantity;
}
