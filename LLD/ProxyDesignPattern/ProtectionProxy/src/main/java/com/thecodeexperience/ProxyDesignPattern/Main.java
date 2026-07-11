package com.thecodeexperience.ProxyDesignPattern;

public class Main {
    public static void main(String[] args) {
        EmployeeDao employeeDao = new EmployeeDaoProxy(new EmployeeDaoImpl());

        try {
            employeeDao.create("USER");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            employeeDao.create("ADMIN");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("-----------------------------");
        try {
            employeeDao.get("USER");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            employeeDao.get("ADMIN");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("-----------------------------");
        try {
            employeeDao.delete("USER");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            employeeDao.delete("ADMIN");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
