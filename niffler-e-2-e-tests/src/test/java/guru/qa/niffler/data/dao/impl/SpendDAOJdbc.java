package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDAOJdbc implements SpendDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement("INSERT INTO spend " +
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
        } catch (SQLException s) {
            throw new RuntimeException(s);
        }
    }


    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("category_id", UUID.class));
                    SpendEntity spend = new SpendEntity();
                    spend.setId(rs.getObject("id", UUID.class));
                    spend.setUsername(rs.getString("username"));
                    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spend.setSpendDate(rs.getDate("spend_date"));
                    spend.setAmount(rs.getDouble("amount"));
                    spend.setDescription(rs.getString("description"));
                    spend.setCategory(categoryEntity);

                    return Optional.of(spend);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ?")) {
            ps.setString(1, username);

            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<SpendEntity> spendEntityList = new ArrayList<>();
                while (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    UUID categoryId = rs.getObject("category_id", UUID.class);

                    if (categoryId != null) {
                        categoryEntity.setId(categoryId);
                    }
                    categoryEntity.setId(rs.getObject("category_id", UUID.class));

                    SpendEntity spend = new SpendEntity();
                    spend.setId(rs.getObject("id", UUID.class));
                    spend.setUsername(rs.getString("username"));
                    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spend.setSpendDate(rs.getDate("spend_date"));
                    spend.setAmount(rs.getDouble("amount"));
                    spend.setDescription(rs.getString("description"));
                    spend.setCategory(categoryEntity);

                    spendEntityList.add(spend);
                }
                return spendEntityList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSpend(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?")) {
            ps.setObject(1, spend.getId());
            int deleted = ps.executeUpdate();

            if (deleted == 0) {
                throw new SQLException("Could not delete spend");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement
                ("SELECT * FROM spend")) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                List<SpendEntity> result = new ArrayList<>();
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();

                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setSpendDate((rs.getDate("spend_date")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(new CategoryEntity(rs.getObject("category_id", UUID.class)));
                    result.add(se);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
