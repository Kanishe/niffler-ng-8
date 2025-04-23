package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataBases {

    private DataBases() {

    }

    public static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    public static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

    public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {
    }

    public record XaConsumer(Consumer<Connection> function, String jdbcUrl) {
    }

    public static <T> T xaTransaction(XaFunction<T>... actions) {
        UserTransaction userTransaction = new UserTransactionImp();
        try {
            userTransaction.begin();
            T result = null;

            for (XaFunction<T> action : actions) {
                result = action.function.apply(getConnection(action.jdbcUrl));
            }
            userTransaction.commit();
            return result;
        } catch (Exception e) {

            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);

            }
            throw new RuntimeException(e);
        }
    }

    public static void xaTransaction(XaConsumer... actions) {
        UserTransaction userTransaction = new UserTransactionImp();
        try {
            userTransaction.begin();

            for (XaConsumer action : actions) {
                action.function.accept(getConnection(action.jdbcUrl));
            }
            userTransaction.commit();

        } catch (Exception e) {

            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);

            }
            throw new RuntimeException(e);
        }
    }

    private static DataSource getDataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(jdbcUrl,
                jbcKey ->
                {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqueId = StringUtils.substringAfter(jbcKey, "5432/");
                    dsBean.setUniqueResourceName(uniqueId);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.setProperty("URL", jdbcUrl);
                    props.setProperty("user", "postgres");
                    props.setProperty("password", "secret");
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(10);
                    return dsBean;


                }
        );
    }

    public static Connection getConnection(String jdbcUrl) throws SQLException {
        return threadConnections
                .computeIfAbsent(
                        Thread.currentThread().threadId(),
                        key -> {
                            try {
                                return new HashMap<>(Map
                                        .of(jdbcUrl,
                                                getDataSource(jdbcUrl).getConnection()));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .computeIfAbsent(
                        jdbcUrl,
                        key -> {
                            try {
                                return getDataSource(jdbcUrl).getConnection();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public static void closeAllConnections() {
        threadConnections.values().forEach(threadCon -> threadCon.forEach(
                (k, connection) -> {
                    try {
                        if (connection != null && connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {

                    }
                }
        ));
    }
}
