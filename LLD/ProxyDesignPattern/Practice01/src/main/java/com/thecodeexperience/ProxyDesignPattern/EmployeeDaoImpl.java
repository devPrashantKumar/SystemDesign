package com.thecodeexperience.ProxyDesignPattern;

public class EmployeeDaoImpl implements EmployeeDao{

    @Override
    public void create(String client) {
        System.out.println("Employee created Successfully");
    }

    @Override
    public void get(String client) {
        System.out.println("Employee fetched Successfully");
    }

    @Override
    public void delete(String client) {
        System.out.println("Employee deleted Successfully");
    }
}
