package org.gestionwebservice.Controllers;

import java.util.Map;

import org.gestionwebservice.Services.AuthenticationService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth") // Define the base path for authentication-related endpoints
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Constructor injection
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * POST endpoint to authenticate user and generate JWT token with roles.
     * 
      @param request 
      @return 
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthenticationRequest request) {
        try {

            String token = authenticationService.authenticateUser(request.getEmail(), request.getPassword());

            if (token != null) {
                return Response.ok()
                        .entity(Map.of("token", token, "message", "Login successful"))
                        .build();
            } else {
                // Invalid credentials
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("message", "Invalid email or password"))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Internal server error handling
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", "An error occurred during authentication"))
                    .build();
        }
    }

    // DTO class for capturing login details
    public static class AuthenticationRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
