package com.thecodeexperience.WithBuilderDesignPattern;

public class StudentWay3 {
    // Required fields
    private final String name;
    private final String rollNo;

    // Optional fields
    private final String address;
    private final String fatherName;
    private final String motherName;
    private final int age;

    public static StudentBuilder builder(String name, String rollNo){
        return new StudentBuilder(name, rollNo);
    }

    private StudentWay3(StudentBuilder builder) {
        this.name = builder.name;
        this.rollNo = builder.rollNo;
        this.address = builder.address;
        this.fatherName = builder.fatherName;
        this.motherName = builder.motherName;
        this.age = builder.age;
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', rollNo='" + rollNo + "', address='" + address
                + "', fatherName='" + fatherName + "', motherName='" + motherName + "', age=" + age + "}";
    }

    public static class StudentBuilder {
        // Required fields — set via constructor so they can never be missing.
        private final String name;
        private final String rollNo;

        // Optional fields — default to null/0 if not provided.
        private String address;
        private String fatherName;
        private String motherName;
        private int age;

        private StudentBuilder(String name, String rollNo) {
            this.name = name;
            this.rollNo = rollNo;
        }

        public StudentBuilder address(String address) {
            this.address = address;
            return this;
        }

        public StudentBuilder fatherName(String fatherName) {
            this.fatherName = fatherName;
            return this;
        }

        public StudentBuilder motherName(String motherName) {
            this.motherName = motherName;
            return this;
        }

        public StudentBuilder age(int age) {
            this.age = age;
            return this;
        }

        public StudentWay3 build() {
            return new StudentWay3(this);
        }
    }
}
