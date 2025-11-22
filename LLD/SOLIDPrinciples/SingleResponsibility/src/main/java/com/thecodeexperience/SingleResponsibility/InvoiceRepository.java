package com.thecodeexperience.SingleResponsibility;

public class InvoiceRepository {
    Invoice invoice;

    public InvoiceRepository(Invoice invoice){
        this.invoice = invoice;
    }

    public void saveToDBInvoice(){
        // save invoice details in DB
        System.out.println("saved invoice details in DB");
    }
}
