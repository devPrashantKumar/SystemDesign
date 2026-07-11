package com.thecodeexperience.ProxyDesignPattern;

public class EmployeeDaoProxy implements EmployeeDao{
    EmployeeDao employeeDao;

    public EmployeeDaoProxy(EmployeeDao employeeDao){
        this.employeeDao = employeeDao;
    }

    @Override
    public void create(String client) throws Exception{
        if(client.equals("USER")) {
            employeeDao.create(client);
            return;
        }
        throw new IllegalAccessException("USER ROLE IS REQUIRED");
    }

    @Override
    public void get(String client) throws Exception {
        if(client.equals("USER") || client.equals("ADMIN")) {
            employeeDao.get(client);
            return;
        }
        throw new IllegalAccessException("USER or ADMIN ROLE IS REQUIRED");
    }

    @Override
    public void delete(String client)throws Exception {
        if(client.equals("ADMIN")) {
            employeeDao.delete(client);
            return;
        }
        throw new IllegalAccessException("ADMIN ROLE IS REQUIRED");
    }
}
