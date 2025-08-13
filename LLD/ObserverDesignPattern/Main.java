package LLD.ObserverDesignPattern;

public class Main {
    public static void main(String[] args) {
        WSObservable wsObservable = new WSObservable();
        GAObservable gaObservable = new GAObservable();
        DisplayObserver displayObserver = new DisplayObserver();
        RadioObserver radioObserver = new RadioObserver();
        wsObservable.add(displayObserver);
        wsObservable.add(radioObserver);
        gaObservable.add(displayObserver);
        gaObservable.add(radioObserver);

        wsObservable.setTemprature(60);
        gaObservable.setAdvisory("Hot weather");
        System.out.println("---------------");
        wsObservable.setTemprature(25);
        gaObservable.setAdvisory("Pleasent weather");
        System.out.println("---------------");
        wsObservable.setTemprature(10);
        gaObservable.setAdvisory("Cool weather");

    }
}
