package model;

import Branch.Entity;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    void save(T t) throws SQLException;

    void delete(T t) throws  SQLException;

    T getById(int id) throws SQLException;

    int getId(T t) throws SQLException;

    List<T> getAll() throws SQLException;
}
