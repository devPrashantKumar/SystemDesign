package com.thecodeexperience.WithBuilderDesignPattern;

public class Main3 {
    public static void main(String[] args) {

        // Client creates the builder (with required fields) and passes it to the Director.
        // Director only decides WHICH optional fields to set — it does not own the builder.

        StudentWay3 scienceStudent = StudentDirector.buildScienceStudent(
                StudentWay3
                        .builder("Prashant", "101")
                        .address("Delhi")
                        .age(21)
        );
        System.out.println(scienceStudent);

        StudentWay3 hostelStudent = StudentDirector.buildHostelStudent(
                StudentWay3
                        .builder("Riya", "102")
                        .age(19)
        );
        System.out.println(hostelStudent);

        StudentWay3 minimalStudent = StudentDirector.buildMinimalStudent(
                StudentWay3.
                        builder("Arjun", "103")
        );
        System.out.println(minimalStudent);
    }
}
