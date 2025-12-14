package service.log;

public interface Logger<T> {
    void record(T action);
    T removeLast();
    void delete();
}
