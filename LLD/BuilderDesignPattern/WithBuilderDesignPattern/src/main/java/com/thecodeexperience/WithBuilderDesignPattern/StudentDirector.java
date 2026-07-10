package com.thecodeexperience.WithBuilderDesignPattern;

// Director knows HOW to build specific preset configurations of a Student.
// It receives the builder from outside (constructor injection) — it does not
// create builders itself. This keeps the Director decoupled from any specific
// builder implementation (standard GoF approach).
public class StudentDirector {

    private final StudentWay3.StudentBuilder builder;

    // Client creates the builder and passes it in — Director only configures it.
    public StudentDirector(StudentWay3.StudentBuilder builder) {
        this.builder = builder;
    }

    // Configures the builder for a science student and returns the product.
    public StudentWay3 buildScienceStudent(String address, int age) {
        return builder
                .address(address)
                .age(age)
                .fatherName("Science-Department-Guardian")
                .build();
    }

    // Configures the builder for a hostel student and returns the product.
    public StudentWay3 buildHostelStudent(int age) {
        return builder
                .address("University Hostel, Block-A")
                .age(age)
                .build();
    }

    // Returns a minimal student with only the required fields.
    public StudentWay3 buildMinimalStudent() {
        return builder.build();
    }
}
