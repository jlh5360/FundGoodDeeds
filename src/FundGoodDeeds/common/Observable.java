package FundGoodDeeds.common;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private final List<Observer<T>> observers = new ArrayList<>();

    public void addObserver(Observer<T> o) {
        if (o != null) observers.add(o);
    }

    public void removeObserver(Observer<T> o) {
        observers.remove(o);
    }

    public void notifyObservers(T event) {
        for (Observer<T> o : observers) {
            try {
                o.update(event);
            } catch (Exception ignored) {
                // keep notifications best-effort
            }
        }
    }
}
