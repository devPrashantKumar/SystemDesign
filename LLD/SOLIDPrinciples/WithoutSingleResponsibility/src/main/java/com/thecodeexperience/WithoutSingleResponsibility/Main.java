package com.thecodeexperience.WithoutSingleResponsibility;

public class Main {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(10,8);
        System.out.println(invoice.calculateInvoics());
        invoice.printInvoice();
        invoice.saveToDBInvoice();
    }
}
