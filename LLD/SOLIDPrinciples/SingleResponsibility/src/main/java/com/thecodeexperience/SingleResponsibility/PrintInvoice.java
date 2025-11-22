package com.thecodeexperience.SingleResponsibility;

public class PrintInvoice {
    Invoice invoice;

    public PrintInvoice(Invoice invoice){
        this.invoice = invoice;
    }
    public void printInvoice(){
        // print invoice pdf
        System.out.println("printed invoice pdf");
    }

}
