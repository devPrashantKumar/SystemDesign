package com.thecodeexperience.FacadeDesignPattern;

// SUBSYSTEM class — handles delivery only. Independent and unaware of the facade.
public class ShippingService {
    public void scheduleDelivery(String item) {
        System.out.println("Scheduling delivery for " + item);
    }
}
