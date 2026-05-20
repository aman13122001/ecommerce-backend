package com.ecommerce.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject OrderService orderService;

    @GET
    public Response getAllOrders() {
        return Response.ok(orderService.getAllOrders()).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) return Response.status(404).entity(Map.of("error","Order not found")).build();
        return Response.ok(order).build();
    }

    @GET
    @Path("/user/{userId}")
    public Response getOrdersByUser(@PathParam("userId") Long userId) {
        return Response.ok(orderService.getOrdersByUser(userId)).build();
    }

    @POST
    public Response placeOrder(Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("items");
            Map<Long, Integer> items = itemsRaw.stream().collect(Collectors.toMap(
                i -> Long.valueOf(i.get("productId").toString()),
                i -> Integer.valueOf(i.get("quantity").toString())
            ));
            return Response.status(201).entity(orderService.placeOrder(userId, items)).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, Map<String, String> body) {
        try {
            return Response.ok(orderService.updateOrderStatus(
                id, Order.Status.valueOf(body.get("status")))).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") Long id) {
        try {
            orderService.cancelOrder(id);
            return Response.ok(Map.of("message", "Order cancelled")).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }
}
