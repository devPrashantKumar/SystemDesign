package LLD.ObserverDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class WSObservable implements Observable {
    int temp=0;
    List<Observer> observers = new ArrayList<>();

    @Override
    public void add(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for(Observer observer:observers){
            observer.update(this);
        }
    }

    public void setTemprature(int temp){
        this.temp = temp;
        notifyObserver();
    }
    
}
