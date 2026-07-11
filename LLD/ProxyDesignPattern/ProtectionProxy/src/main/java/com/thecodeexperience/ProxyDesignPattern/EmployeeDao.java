package com.thecodeexperience.ProxyDesignPattern;

public interface EmployeeDao {
    void create(String client) throws Exception;
    void get(String client) throws Exception;
    void delete(String client) throws Exception ;
}
