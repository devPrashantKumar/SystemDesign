package com.thecodeexperience.WithoutSingleResponsibility;

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

    public void printInvoice(){
        // print invoice pdf
        System.out.println("printed invoice pdf");
    }

    public void saveToDBInvoice(){
        // save invoice details in DB
        System.out.println("saved invoice details in DB");
    }
}
