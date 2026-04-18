package model;

import Branch.Entity;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    void save(T t);

    void delete(T t);

    void update(T t);

    T getById(int id);

    List<T> getAll();
}
