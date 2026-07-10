package com.thecodeexperience.FacadeDesignPattern;

// CLIENT — depends only on the facade. It has no idea that inventory, payment,
// and shipping are separate services, nor in what order they must run.
// One method call is its entire view of "placing an order".
public class Client {
    OrderFacade orderFacade;

    Client(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    void placeOrder() {
        boolean success = orderFacade.placeOrder("item", "card", 10);
        System.out.println("Client sees only the result: " + success);
    }
}
