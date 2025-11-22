package com.thecodeexperience.WithoutDependecyInversionPrinciple;

public class DataStore {
    private MySQLDatabase db = new MySQLDatabase();

    public void save(String data) {
        db.save(data);
    }
}
