package com.thecodeexperience.Practice;

public class DisplayObserver implements Observer{

    @Override
    public void update(Observable observable) {
        if(observable instanceof WSObservable){
            System.out.println("Temprature Updated by Weather Station");
            System.out.println("Display - New Temprature : "+((WSObservable) observable).temp);
        }
        else if(observable instanceof GAObservable){
            System.out.println("Advisory Issued by Government");
            System.out.println("Display - New Advisory : "+((GAObservable) observable).advisory);
        }
    }

//    public void update(Observable observable, Class<?> sourceClass) {
//        if(sourceClass.equals(WSObservable.class)){
//            System.out.println("Temprature Updated by Weather Station");
//            System.out.println("Display - New Temprature : "+((WSObservable) observable).temp);
//        }
//        else if(sourceClass.equals(GAObservable.class)){
//            System.out.println("Advisory Issued by Government");
//            System.out.println("Display - New Advisory : "+((GAObservable) observable).advisory);
//        }
//    }
}
