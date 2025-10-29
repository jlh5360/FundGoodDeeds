package FundGoodDeeds.model;

import java.util.*;

import FundGoodDeeds.view.Observer;
public class Observable<T> {
    private final List<Observer<T>> obs = new ArrayList<>();
    public void addObserver(Observer<T> o){ if(o!=null) obs.add(o); }
    public void notifyObservers(T arg){ for(var o: obs) o.update(arg); }
}