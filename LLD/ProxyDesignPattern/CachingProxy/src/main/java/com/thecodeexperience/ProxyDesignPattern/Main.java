package com.thecodeexperience.ProxyDesignPattern;

public class Main {
    public static void main(String[] args) {
        DataService service = new CachingDataServiceProxy(new RealDataService());

        System.out.println("fetch A (1st) -> " + service.fetchData("A"));   // miss -> expensive
        System.out.println("fetch A (2nd) -> " + service.fetchData("A"));   // hit  -> cheap
        System.out.println("fetch B (1st) -> " + service.fetchData("B"));   // miss -> expensive
        System.out.println("fetch A (3rd) -> " + service.fetchData("A"));   // hit  -> cheap
    }
}
