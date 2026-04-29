package DB;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    private static HikariConfig config = new HikariConfig("db.properties");
    private static HikariDataSource ds = new HikariDataSource(config);

    private DB() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}