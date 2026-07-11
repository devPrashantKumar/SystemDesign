package com.thecodeexperience.PrototypeDesignPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Employee employee = new Employee("Prashant","JVM", new ArrayList<>(Arrays.asList("JAVA", "AWS", "PYTHON")));
        System.out.println(employee);
        Employee employee1 = employee.cloneObject();
        employee.addSkills("DESIGN PATTERNS");
        System.out.println(employee1);

    }
}
