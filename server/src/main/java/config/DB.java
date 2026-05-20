package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DB {

    private static HikariConfig config = new HikariConfig("db.properties");
    private static HikariDataSource ds = new HikariDataSource(config);

    private DB() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}