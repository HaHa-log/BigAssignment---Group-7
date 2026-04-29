package model.impl;

import Branch.Item;
import DB.DB;
import DB.DbException;
import model.ItemsDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class ItemsDAOImpl implements ItemsDAO {
    @Override
    public void save(Item item) {
        String sql = "INSERT INTO items "
                + "(name, startingPrice, description)"
                + "VALUES (?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, item.getName());
            st.setDouble(2, item.getStartingPrice());
            st.setString(3, item.getDescription());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        // Cách dùng chuẩn cho JDBC hiện đại
                        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
                        item.setId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }

            System.out.println("[System]: Item " + item.getName() + " saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Item item) {
        String sql = "DELETE FROM items WHERE user_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, item.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("User is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Item item) {
        String sql = "UPDATE users "
                +"SET name = ?, startingPrice = ?, description = ?, status = ? "
                + " WHERE items_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, item.getName());
            st.setDouble(2, item.getStartingPrice());
            st.setString(3, item.getDescription());
            st.setString(4, item.getStatus().name());
            st.setInt(5, item.getId());
            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public Item getById(int id) {
        return null;
    }

    @Override
    public List<Item> getAll() {
        return List.of();
    }
}
