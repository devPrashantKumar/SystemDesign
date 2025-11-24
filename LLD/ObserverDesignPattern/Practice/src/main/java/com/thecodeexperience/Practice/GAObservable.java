package com.thecodeexperience.Practice;

import java.util.ArrayList;
import java.util.List;

public class GAObservable implements Observable{
    String advisory="";
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
            //observer.update(this,this.getClass());
        }
    }

    public void setAdvisory(String advisory){
        this.advisory = advisory;
        notifyObserver();
    }
}
