package com.ecommerce.graphql;

import com.ecommerce.auth.AuthService;
import com.ecommerce.kafka.OrderEventProducer;
import com.ecommerce.dto.*;
import com.ecommerce.order.Order;
import com.ecommerce.order.OrderItem;
import com.ecommerce.product.Product;
import com.ecommerce.user.User;
import com.ecommerce.search.ProductSearchService;
import com.ecommerce.cart.CartService;
import com.ecommerce.cart.CartItem;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.graphql.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@GraphQLApi
@ApplicationScoped
public class EcommerceGraphQLApi {

    @Inject
    OrderEventProducer producer;

    @Inject
    ProductSearchService productSearchService;

    @Inject
    CartService cartService;

    @Inject
    AuthService authService;

    @Query
    @Name("products")
    public List<Product> products() {
        return Product.listAll();
    }

    @Query
    @Name("product")
    public Product product(@Name("id") Long id) {
        return Product.findById(id);
    }

    @Query
    @Name("searchProducts")
    public List<Product> searchProducts(@Name("query") String query) {
        return productSearchService.searchProducts(query);
    }

    @Query
    @Name("getCart")
    public List<CartItem> getCart(@Name("userId") String userId) {
        return cartService.getCart(userId);
    }

    @Query
    @Name("orders")
    public List<Order> orders(@Name("userId") Long userId) {
        return Order.find("user.id", userId).list();
    }

    @Query
    @Name("order")
    public Order order(@Name("id") Long id) {
        return Order.findById(id);
    }

    @Query
    @Name("me")
    public User me() {
        return User.findById(1L);
    }

    @Mutation
    @Name("createProduct")
    @Transactional
    public Product createProduct(ProductInput input) {
        Product product = new Product();
        product.name = input.name;
        product.description = input.description;
        product.price = input.price;
        product.category = input.category;
        product.stockQuantity = input.stockQuantity;
        product.persist();
        return product;
    }

    @Mutation
    @Name("updateProduct")
    @Transactional
    public Product updateProduct(@Name("id") Long id, ProductInput input) {
        Product product = Product.findById(id);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        product.name = input.name;
        product.description = input.description;
        product.price = input.price;
        product.category = input.category;
        product.stockQuantity = input.stockQuantity;

        return product;
    }

    @Mutation
    @Name("addToCart")
    public String addToCart(
            @Name("userId") String userId,
            @Name("productId") Long productId,
            @Name("productName") String productName,
            @Name("price") BigDecimal price,
            @Name("quantity") int quantity
    ) {
        CartItem item = new CartItem();
        item.productId = productId;
        item.productName = productName;
        item.price = price;
        item.quantity = quantity;

        return cartService.addToCart(userId, item);
    }

    @Mutation
    @Name("clearCart")
    public String clearCart(@Name("userId") String userId) {
        return cartService.clearCart(userId);
    }

    @Mutation
    @Name("registerUser")
    @Transactional
    public User registerUser(RegisterRequest request) {
        User user = new User();
        user.email = request.email;
        user.passwordHash = request.password;
        user.fullName = request.fullName;
        user.persist();
        return user;
    }

    @Mutation
    @Name("login")
    public String login(LoginRequest request) {
        User user = User.find("email", request.email).firstResult();

        if (user == null) {
            throw new RuntimeException("Invalid email");
        }

        if (!user.passwordHash.equals(request.password)) {
            throw new RuntimeException("Invalid password");
        }

        return authService.generateToken(user);
    }

    @Mutation
    @Name("placeOrder")
    @RolesAllowed("CUSTOMER")
    @Transactional
    public Order placeOrder(PlaceOrderInput input) {
        User user = User.findById(input.userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Order order = new Order();
        order.user = user;
        order.items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (PlaceOrderInput.OrderItemInput itemInput : input.items) {
            Product product = Product.findById(itemInput.productId);

            if (product == null) {
                throw new RuntimeException("Product not found");
            }

            OrderItem item = new OrderItem();
            item.order = order;
            item.product = product;
            item.quantity = itemInput.quantity;
            item.unitPrice = product.price;

            order.items.add(item);

            total = total.add(product.price.multiply(BigDecimal.valueOf(itemInput.quantity)));
        }

        order.totalAmount = total;
        order.persist();

        producer.sendOrderEvent("ORDER_PLACED -> Order ID: " + order.id);

        return order;
    }
}
