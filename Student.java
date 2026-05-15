package com.sms;

public class Student {
    private int id;
    private String rollNo, name, email, course, phone;
    private int year;

    public Student() {}
    public Student(int id, String rollNo, String name, String email, String course, int year, String phone) {
        this.id = id; this.rollNo = rollNo; this.name = name; this.email = email;
        this.course = course; this.year = year; this.phone = phone;
    }

    public int getId() { return id; }
    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public int getYear() { return year; }
    public String getPhone() { return phone; }

    public void setId(int id) { this.id = id; }
    public void setRollNo(String r) { this.rollNo = r; }
    public void setName(String n) { this.name = n; }
    public void setEmail(String e) { this.email = e; }
    public void setCourse(String c) { this.course = c; }
    public void setYear(int y) { this.year = y; }
    public void setPhone(String p) { this.phone = p; }
}
