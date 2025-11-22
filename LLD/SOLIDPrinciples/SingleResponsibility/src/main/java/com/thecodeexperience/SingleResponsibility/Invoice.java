package com.thecodeexperience.SingleResponsibility;

public class Invoice {
    int quantity;
    int price;

    public Invoice(int quantity, int price){
        this.quantity = quantity;
        this.price = price;
    }

    public int calculateInvoics(){
        return quantity*price;
    }
}
