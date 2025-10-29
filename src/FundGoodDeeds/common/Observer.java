package FundGoodDeeds.view;

@FunctionalInterface
public interface Observer<T> { 
    void update(T arg); 
}