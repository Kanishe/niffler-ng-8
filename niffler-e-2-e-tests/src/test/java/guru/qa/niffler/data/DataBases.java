package guru.qa.niffler.data;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataBases {

    private DataBases() {

    }

    public static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    private static DataSource getDataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(jdbcUrl, jbcKey ->
                {
                    PGSimpleDataSource dataSource = new PGSimpleDataSource();
                    dataSource.setUser("postgres");
                    dataSource.setPassword("secret");
                    dataSource.setURL(jdbcUrl);
                    return dataSource;
                }
        );
    }

    public static Connection getConnection(String jdbcUrl) throws SQLException {
        return getDataSource(jdbcUrl).getConnection();
    }
}
