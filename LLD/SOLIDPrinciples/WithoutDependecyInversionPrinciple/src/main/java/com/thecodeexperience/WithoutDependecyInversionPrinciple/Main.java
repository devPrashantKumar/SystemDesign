package com.thecodeexperience.WithoutDependecyInversionPrinciple;

public class Main {

    public static void main(String[] args) {
        DataStore dataStore = new DataStore();
        dataStore.save("hello");
    }
}
