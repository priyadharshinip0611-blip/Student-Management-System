package com.sms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students(roll_no,name,email,course,year,phone) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getCourse());
            ps.setInt(5, s.getYear());
            ps.setString(6, s.getPhone());
            ps.executeUpdate();
        }
    }

    public void updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET roll_no=?,name=?,email=?,course=?,year=?,phone=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getCourse());
            ps.setInt(5, s.getYear());
            ps.setString(6, s.getPhone());
            ps.setInt(7, s.getId());
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Student> getAllStudents(String search) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE roll_no LIKE ? OR name LIKE ? OR email LIKE ? OR course LIKE ? ORDER BY id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + (search == null ? "" : search) + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q); ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("course"),
                        rs.getInt("year"),
                        rs.getString("phone")
                    ));
                }
            }
        }
        return list;
    }
}
