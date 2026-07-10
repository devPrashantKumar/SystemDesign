package com.thecodeexperience.WithoutBuilderDesignPattern;

public class Main {
    public static void main(String[] args) {

        // Problem 1: You must remember the exact argument order.
        // What does the 4th argument mean? Hard to tell without checking the constructor.
        Student s1 = new Student("Prashant", "101", "Delhi", "Ramesh", "Sunita", 21);
        System.out.println(s1);

        // Problem 2: Optional fields force you to pass null explicitly.
        // This is error-prone and unreadable.
        Student s2 = new Student("Riya", "102", null, null, null, 0);
        System.out.println(s2);

        // Problem 3: Adding a new optional field (e.g., phone number) means adding
        // yet another constructor overload — the list of constructors keeps growing.
    }
}

