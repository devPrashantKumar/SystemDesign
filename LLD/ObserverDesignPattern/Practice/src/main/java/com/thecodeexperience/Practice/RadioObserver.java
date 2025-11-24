package com.thecodeexperience.Practice;

public class RadioObserver implements Observer{

    @Override
    public void update(Observable observable) {
        if(observable instanceof WSObservable){
            System.out.println("Temperature Updated by Weather Station");
            System.out.println("Radio - New Temperature : "+((WSObservable) observable).temp);
        }
        else if(observable instanceof GAObservable){
            System.out.println("Advisory Issued by Government");
            System.out.println("Radio - New Advisory : "+((GAObservable) observable).advisory);
        }
    }
}
