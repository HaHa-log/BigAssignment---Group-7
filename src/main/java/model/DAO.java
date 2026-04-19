package model;

import java.util.List;

public interface DAO<T> {

    void save(T t);

    void delete(T t);

    void update(T t);

    T getById(int id);

    List<T> getAll();
}
