package com.ecommerce.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Map;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject ProductService productService;

    @GET
    public Response getAllProducts() {
        return Response.ok(productService.getAllProducts()).build();
    }

    @GET
    @Path("/{id}")
    public Response getProduct(@PathParam("id") Long id) {
        Product p = productService.getProductById(id);
        if (p == null) return Response.status(404).entity(Map.of("error","Product not found")).build();
        return Response.ok(p).build();
    }

    @GET
    @Path("/category/{category}")
    public Response getByCategory(@PathParam("category") String category) {
        return Response.ok(productService.getProductsByCategory(category)).build();
    }

    @GET
    @Path("/search/{keyword}")
    public Response search(@PathParam("keyword") String keyword) {
        return Response.ok(productService.searchProducts(keyword)).build();
    }

    @POST
    public Response createProduct(Map<String, String> body) {
        try {
            Product p = productService.createProduct(
                body.get("name"), body.get("description"),
                new BigDecimal(body.get("price")),
                body.get("category"),
                Integer.parseInt(body.get("stockQuantity"))
            );
            return Response.status(201).entity(p).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Map<String, String> body) {
        try {
            Product p = productService.updateProduct(id,
                body.get("name"), body.get("description"),
                body.containsKey("price") ? new BigDecimal(body.get("price")) : null,
                body.get("category"),
                body.containsKey("stockQuantity") ? Integer.parseInt(body.get("stockQuantity")) : -1
            );
            return Response.ok(p).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        return productService.deleteProduct(id)
            ? Response.noContent().build()
            : Response.status(404).build();
    }
}
