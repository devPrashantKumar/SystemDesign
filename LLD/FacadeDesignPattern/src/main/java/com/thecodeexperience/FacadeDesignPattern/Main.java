package com.thecodeexperience.FacadeDesignPattern;

// Composition root — wires the client to the facade and kicks off the flow.
public class Main {

    public static void main(String[] args) {
        Client client = new Client(new OrderFacade());
        client.placeOrder();
    }
}
