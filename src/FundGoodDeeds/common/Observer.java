package FundGoodDeeds.common;

@FunctionalInterface
public interface Observer<T> {
    void update(T event);
}
