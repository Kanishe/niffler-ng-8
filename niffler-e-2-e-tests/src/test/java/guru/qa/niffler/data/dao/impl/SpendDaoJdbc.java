package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.*;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
    private static final Config CONFIG = Config.getInstance();

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (Connection connection = DataBases.getConnection(CONFIG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO spend " +
                    "(username, spend_date, currency, amount, description, category_id)" + " VALUES (?, ?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
//                ps.setObject(1, spend.getId());
                ps.setString(1, spend.getUsername());
                ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());
                ps.executeUpdate();

                ps.getResultSet();

                final UUID generatedId;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Could not get generated ID");
                    }
                }
                spend.setId(generatedId);
                return spend;
            }
        } catch (SQLException s) {
            throw new RuntimeException(s);
        }
    }
}
