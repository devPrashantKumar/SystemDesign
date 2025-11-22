
package com.thecodeexperience.DependecyInversionPrinciple;

public class MySQLDatabase implements Database {
    public void save(String data) {
        System.out.println("data saved : "+data);
    }
}
