package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final SpendDAO spendDAOJdbc = new SpendDAOJdbc();
    private final CategoryDAO categoryDAOJdbc = new CategoryDAOJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        if (spend.getCategory().getId() == null
                || categoryDAOJdbc.findCategoryById(spend.getCategory().getId()).isEmpty()
        ) {
            categoryDAOJdbc.create(spend.getCategory());
        }
        return spendDAOJdbc.createSpend(spend);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                           SELECT s.id, s.username, s.spend_date, s.currency, s.amount, s.description, c.id as category_id,
                           c.name as category_name, c.archived as category_archived
                           FROM spend s JOIN category c on s.category_id = c.id
                           WHERE s.id = ?
                        """
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate((rs.getDate("spend_date")));
                    se.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    ce.setName(rs.getString("category_name"));
                    ce.setArchived(rs.getBoolean("category_archived"));
                    se.setCategory(ce);
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        categoryDAOJdbc.update(spend.getCategory());
        return spendDAOJdbc.update(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        categoryDAOJdbc.create(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDAOJdbc.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDAOJdbc.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDAOJdbc.findSpendById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDAOJdbc.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDAOJdbc.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDAOJdbc.deleteCategory(category);
    }
}
