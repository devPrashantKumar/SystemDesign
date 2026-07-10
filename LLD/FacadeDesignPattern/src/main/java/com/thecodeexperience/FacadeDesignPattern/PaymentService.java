package com.thecodeexperience.FacadeDesignPattern;

// SUBSYSTEM class — handles payment only. Independent and unaware of the facade.
public class PaymentService {
    public boolean charge(String card, double amount) {
        System.out.println("Charging " + amount + " to " + card);
        return true;
    }
}
