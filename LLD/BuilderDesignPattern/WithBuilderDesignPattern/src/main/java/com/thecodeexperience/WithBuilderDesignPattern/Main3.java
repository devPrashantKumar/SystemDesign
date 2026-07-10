package com.thecodeexperience.WithBuilderDesignPattern;

public class Main3 {
    public static void main(String[] args) {

        // Client creates the builder (with required fields) and passes it to the Director.
        // Director only decides WHICH optional fields to set — it does not own the builder.

        StudentWay3 scienceStudent = new StudentDirector(StudentWay3.builder("Prashant", "101"))
                .buildScienceStudent("Delhi", 21);
        System.out.println(scienceStudent);

        StudentWay3 hostelStudent = new StudentDirector(StudentWay3.builder("Riya", "102"))
                .buildHostelStudent(19);
        System.out.println(hostelStudent);

        StudentWay3 minimalStudent = new StudentDirector(StudentWay3.builder("Arjun", "103"))
                .buildMinimalStudent();
        System.out.println(minimalStudent);
    }
}
