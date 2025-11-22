package com.thecodeexperience.SingleResponsibility;


public class Main {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(10,8);
        PrintInvoice printInvoice = new PrintInvoice(invoice);
        InvoiceRepository invoiceRepository = new InvoiceRepository(invoice);

        System.out.println(invoice.calculateInvoics());
        printInvoice.printInvoice();
        invoiceRepository.saveToDBInvoice();
    }}
