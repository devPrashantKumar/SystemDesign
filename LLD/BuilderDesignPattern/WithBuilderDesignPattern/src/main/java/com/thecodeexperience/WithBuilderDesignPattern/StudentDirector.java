package com.thecodeexperience.WithBuilderDesignPattern;

// Director knows HOW to build specific preset configurations of a Student.
// It receives the builder from outside (constructor injection) — it does not
// create builders itself. This keeps the Director decoupled from any specific
// builder implementation (standard GoF approach).
public class StudentDirector {
    // All methods are static — prevent instantiation.
    private StudentDirector() {
    }

    // Configures the builder for a science student and returns the product.
    public static StudentWay3 buildScienceStudent(StudentWay3.StudentBuilder builder) {
        return builder
                .fatherName("Science-Department-Guardian")
                .build();
    }

    // Configures the builder for a hostel student and returns the product.
    public static StudentWay3 buildHostelStudent(StudentWay3.StudentBuilder builder) {
        return builder
                .address("University Hostel, Block-A")
                .build();
    }

    // Returns a minimal student with only the required fields.
    public static StudentWay3 buildMinimalStudent(StudentWay3.StudentBuilder builder) {
        return builder.build();
    }
}
