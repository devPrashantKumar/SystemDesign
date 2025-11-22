package com.thecodeexperience.DependecyInversionPrinciple;

public class Main {

    public static void main(String[] args) {
        Database db = new MySQLDatabase();
        DataStore dataStore = new DataStore(db);
        dataStore.save("hello");
    }
}
