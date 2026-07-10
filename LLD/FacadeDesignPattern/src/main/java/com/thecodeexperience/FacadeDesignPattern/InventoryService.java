package com.thecodeexperience.FacadeDesignPattern;

// SUBSYSTEM class — does one job (inventory) and knows nothing about the facade,
// the other services, or the overall order workflow.
public class InventoryService {
    public boolean checkStock(String item) {
        System.out.println("Checking stock for " + item);
        return true;
    }
}
