package com.example.student.resource;

import com.example.student.model.Course;
import com.example.student.model.CourseStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

    @POST
    public Response addCourse(Course course) {
        String sql = "INSERT INTO courses (name, credits, status) VALUES (?, ?, ?)";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getCredits());
            stmt.setString(3, course.getStatus().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setId(generatedKeys.getLong(1));
                    }
                }
                return Response.status(Response.Status.CREATED).entity(course).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
        return Response.serverError().build();
    }

    @GET
    public Response getCourses(@QueryParam("name") String name) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        if (name != null && !name.isEmpty()) {
            sql += " WHERE name LIKE ?";
        }

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (name != null && !name.isEmpty()) {
                stmt.setString(1, "%" + name + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course();
                    course.setId(rs.getLong("id"));
                    course.setName(rs.getString("name"));
                    course.setCredits(rs.getInt("credits"));
                    course.setStatus(CourseStatus.valueOf(rs.getString("status")));
                    courses.add(course);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
        return Response.ok(courses).build();
    }

    @GET
    @Path("/{id}")
    public Response getCourseById(@PathParam("id") Long id) {
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setId(rs.getLong("id"));
                    course.setName(rs.getString("name"));
                    course.setCredits(rs.getInt("credits"));
                    course.setStatus(CourseStatus.valueOf(rs.getString("status")));
                    return Response.ok(course).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Course with ID " + id + " not found")
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
    }
}
