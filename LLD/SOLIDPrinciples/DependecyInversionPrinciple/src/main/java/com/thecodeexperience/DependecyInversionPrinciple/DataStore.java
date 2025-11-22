package com.thecodeexperience.DependecyInversionPrinciple;

public class DataStore {
    private Database database;

    public DataStore(Database database){
        this.database = database;
    }
    public void save(String data) {
        database.save(data);
    }
}
