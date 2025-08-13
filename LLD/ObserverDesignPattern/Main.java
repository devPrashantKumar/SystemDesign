package LLD.ObserverDesignPattern;

public class Main {
    public static void main(String[] args) {
        WSObservable wsObservable = new WSObservable();
        DisplayObserver displayObserver = new DisplayObserver();
        wsObservable.add(displayObserver);
        wsObservable.setTemprature(100);
        wsObservable.setTemprature(150);
        wsObservable.setTemprature(20);
    }
}
