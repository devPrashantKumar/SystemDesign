package LLD.ObserverDesignPattern;

public class DisplayObserver implements Observer{

    @Override
    public void update(Observable observable) {
        if(observable instanceof WSObservable){
            System.out.println("Temprature Updated and Displayed on Display");
            System.out.println("New Temprature : "+((WSObservable) observable).temp);
        }
    }
}
