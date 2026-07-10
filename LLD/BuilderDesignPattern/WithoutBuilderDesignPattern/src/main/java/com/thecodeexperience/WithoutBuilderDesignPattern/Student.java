package com.thecodeexperience.WithoutBuilderDesignPattern;

// Problem: telescoping constructors — caller must remember argument order.
// Optional fields force many constructor overloads or null placeholders.
public class Student {
    String name;
    String rollNo;
    String address;
    String fatherName;
    String motherName;
    int age;

    // Must provide all fields even when some are optional.
    Student(String name, String rollNo, String address, String fatherName, String motherName, int age) {
        this.name = name;
        this.rollNo = rollNo;
        this.address = address;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.age = age;
    }

    // Overload when fatherName/motherName are unknown — caller must pass null.
    Student(String name, String rollNo, String address, int age) {
        this(name, rollNo, address, null, null, age);
    }

    // Overload when age is also not needed.
    Student(String name, String rollNo) {
        this(name, rollNo, null, null, null, 0);
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', rollNo='" + rollNo + "', address='" + address
                + "', fatherName='" + fatherName + "', motherName='" + motherName + "', age=" + age + "}";
    }
}

