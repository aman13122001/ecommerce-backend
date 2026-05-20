package com.ecommerce.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject UserService userService;

    @GET
    public Response getAllUsers() {
        return Response.ok(userService.getAllUsers()).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) return Response.status(404).entity(Map.of("error","User not found")).build();
        return Response.ok(user).build();
    }

    @POST
    @Path("/register")
    public Response register(Map<String, String> body) {
        try {
            User user = userService.createUser(
                body.get("email"), body.get("fullName"), body.get("password"));
            return Response.status(201).entity(user).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, Map<String, String> body) {
        try {
            return Response.ok(userService.updateUser(id, body.get("fullName"))).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        return userService.deleteUser(id)
            ? Response.noContent().build()
            : Response.status(404).build();
    }
}
