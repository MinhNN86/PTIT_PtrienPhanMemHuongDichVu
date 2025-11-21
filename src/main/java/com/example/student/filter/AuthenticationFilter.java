package com.example.student.filter;

import com.example.student.resource.AuthResource;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Protect /api/students endpoints for POST, PUT, DELETE
        if (path.startsWith("students") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
            String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Missing or invalid Authorization header").build());
                return;
            }

            String token = authHeader.substring("Bearer ".length()).trim();

            try {
                Jwts.parserBuilder().setSigningKey(AuthResource.KEY).build().parseClaimsJws(token);
            } catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build());
            }
        }
    }
}
