package interfaces;

import java.util.List;

public interface Services<T> {
    void add(T t);
    List<T> getAll();
    void delete(T t);
    void update(T t);
    T getById(int id);
}
