package com.thecodeexperience.FacadeDesignPattern;

// FACADE — the single, simplified entry point to the whole ordering subsystem.
// It owns the subsystem services and, more importantly, owns the WORKFLOW:
// the correct order of steps and the rules that connect them
// (don't charge if out of stock, don't ship if payment failed).
// The client calls one method and never sees the moving parts.
public class OrderFacade {
    private final InventoryService inventory = new InventoryService();
    private final PaymentService payment = new PaymentService();
    private final ShippingService shipping = new ShippingService();

    // One simple call hides three services and the rules that tie them together.
    // Each step is guarded — the facade short-circuits the workflow on any failure,
    // so we never charge for an out-of-stock item or ship an unpaid one.
    public boolean placeOrder(String item, String card, double amount) {
        if (!inventory.checkStock(item)) {
            System.out.println("Order failed: " + item + " is out of stock");
            return false;
        }
        if (!payment.charge(card, amount)) {
            System.out.println("Order failed: payment declined");
            return false;
        }
        shipping.scheduleDelivery(item);
        System.out.println("Order placed successfully for " + item);
        return true;
    }
}
