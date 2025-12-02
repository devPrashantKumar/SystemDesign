package com.thecodeexperience.ProxyDesignPattern;

public interface EmployeeDao {
    public void create(String client) throws Exception;
    public void get(String client) throws Exception;
    public void delete(String client) throws Exception ;
}
