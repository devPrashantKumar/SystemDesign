package com.thecodeexperience.WithBuilderDesignPattern;

public class Main2 {
    public static void main(String[] args) {

        // Full student — every field is named, order doesn't matter.
        StudentWay2 s1 = StudentWay2.builder("Prashant", "101")
                .address("Delhi")
                .fatherName("Ramesh")
                .motherName("Sunita")
                .age(21)
                .build();
        System.out.println(s1);

        // Partial student — only set what you know, skip the rest.
        // No nulls, no confusing overloads.
        StudentWay2 s2 = StudentWay2.builder("Riya", "102")
                .age(19)
                .build();
        System.out.println(s2);

        // Adding a new optional field (e.g., phone number) only requires:
        //   1. A new field in Student + StudentBuilder
        //   2. A new setter method in StudentBuilder
        // No existing callers break.
    }
}
