package com.example.student.resource;

import com.example.student.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    // Key should be stronger and stored securely in production
    public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @POST
    @Path("/login")
    public Response login(User user) {
        // 1. Check Hardcoded Admin
        if ("admin".equals(user.getUsername()) && "123456".equals(user.getPassword())) {
            return generateToken(user.getUsername(), "ADMIN");
        }

        // 2. Check Database for Student
        if (checkStudentCredentials(user.getUsername(), user.getPassword())) {
            return generateToken(user.getUsername(), "STUDENT");
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
    }

    private boolean checkStudentCredentials(String username, String password) {
        String sql = "SELECT id FROM students WHERE student_code = ? AND password = ?";
        try (java.sql.Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Response generateToken(String username, String role) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(KEY)
                .compact();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return Response.ok(response).build();
    }
}
