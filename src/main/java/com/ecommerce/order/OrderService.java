package com.ecommerce.order;

import com.ecommerce.product.Product;
import com.ecommerce.user.User;
import com.ecommerce.kafka.OrderEventProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderEventProducer eventProducer;

    public List<Order> getAllOrders() {
        return Order.listAll();
    }

    public Order getOrderById(Long id) {
        return Order.findById(id);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return Order.findByUserId(userId);
    }

    @Transactional
    public Order placeOrder(Long userId, Map<Long, Integer> productQuantities) {
        User user = User.findById(userId);
        if (user == null) throw new RuntimeException("User not found: " + userId);

        Order order = new Order();
        order.user = user;
        order.status = Order.Status.PLACED;
        order.totalAmount = BigDecimal.ZERO;
        order.persist();

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Product product = Product.findById(entry.getKey());
            if (product == null)
                throw new RuntimeException("Product not found: " + entry.getKey());
            if (product.stockQuantity < entry.getValue())
                throw new RuntimeException("Insufficient stock for: " + product.name);

            OrderItem item = new OrderItem();
            item.order = order;
            item.product = product;
            item.quantity = entry.getValue();
            item.unitPrice = product.price;
            item.persist();

            product.stockQuantity -= entry.getValue();
            total = total.add(product.price.multiply(BigDecimal.valueOf(entry.getValue())));
        }
        order.totalAmount = total;
        eventProducer.sendOrderEvent("ORDER_PLACED::" + order.id + "::user=" + userId);
        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.Status status) {
        Order order = Order.findById(orderId);
        if (order == null) throw new RuntimeException("Order not found: " + orderId);
        order.status = status;
        eventProducer.sendOrderEvent("ORDER_STATUS_UPDATED::" + orderId + "::" + status);
        return order;
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = Order.findById(orderId);
        if (order == null) throw new RuntimeException("Order not found: " + orderId);
        if (order.status == Order.Status.SHIPPED || order.status == Order.Status.DELIVERED)
            throw new RuntimeException("Cannot cancel order in status: " + order.status);
        order.status = Order.Status.CANCELLED;
        eventProducer.sendOrderEvent("ORDER_CANCELLED::" + orderId);
        return true;
    }
}
