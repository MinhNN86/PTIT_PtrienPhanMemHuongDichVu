package com.example.student.resource;

import com.example.student.model.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    @GET
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getLong("id"));
                student.setFullName(rs.getString("full_name"));
                student.setStudentCode(rs.getString("student_code"));
                student.setMajor(rs.getString("major"));
                student.setPassword(rs.getString("password"));
                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    @POST
    public Response addStudent(Student student) {
        String sql = "INSERT INTO students (full_name, student_code, major, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getFullName());
            stmt.setString(2, student.getStudentCode());
            stmt.setString(3, student.getMajor());
            stmt.setString(4, student.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getLong(1));
                    }
                }
                return Response.status(Response.Status.CREATED).entity(student).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
        return Response.serverError().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") Long id, Student updatedStudent) {
        String sql = "UPDATE students SET full_name = ?, student_code = ?, major = ?, password = ? WHERE id = ?";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedStudent.getFullName());
            stmt.setString(2, updatedStudent.getStudentCode());
            stmt.setString(3, updatedStudent.getMajor());
            stmt.setString(4, updatedStudent.getPassword());
            stmt.setLong(5, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                updatedStudent.setId(id);
                return Response.ok(updatedStudent).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") Long id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = com.example.student.util.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Student not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Database error").build();
        }
    }
}
