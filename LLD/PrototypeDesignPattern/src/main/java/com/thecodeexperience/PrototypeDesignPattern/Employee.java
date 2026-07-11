package com.thecodeexperience.PrototypeDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Employee implements Prototype {
    private String name;
    private String department;
    private List<String> skills;

    public Employee(String name, String department, List<String> skills) {
        this.name = name;
        this.department = department;
        this.skills = skills;
    }

    public void addSkills(String skills) {
        this.skills.add(skills);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", skills=" + skills +
                '}';
    }

    @Override
    public Employee cloneObject() {
        // deep copy the mutable list
        return new Employee(this.name, this.department, new ArrayList<>(this.skills));
    }

}
