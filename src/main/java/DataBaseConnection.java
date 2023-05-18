import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class DataBaseConnection {

    private static final Set<String> TABLES = Set.of("homework", "lesson");
    private static final String DB_PROPS = "db.properties";
    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";

    private static Properties loadProperties() {
        try (InputStream is = DataBaseConnection.class.getClassLoader().getResourceAsStream(DB_PROPS)) {
            Properties dbProperties = new Properties();
            dbProperties.load(is);
            return dbProperties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource initDataSource() {
        Properties props = loadProperties();
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(props.getProperty(DB_URL));
        dataSource.setUser(props.getProperty(DB_USERNAME));
        dataSource.setPassword(props.getProperty(DB_PASSWORD));
        return dataSource;
    }

    public static Connection getConnection() {
        DataSource dataSource = initDataSource();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, null, null);
            while (tables.next()) {
                if (TABLES.contains(tables.getString(3))) {
                    System.out.println("Table = " + tables.getString(3));
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close (Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}