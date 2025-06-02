package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final SpendDAO spendDAOSpringJdbc = new SpendDAOSpringJdbc();
    private final CategoryDAO categoryDAOSpringJdbc = new CategoryDAOSpringJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        if (spend.getCategory().getId() == null) {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO category (username, name, archived) " +
                                        "VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, spend.getCategory().getUsername());
                        ps.setString(2, spend.getCategory().getName());
                        ps.setBoolean(3, spend.getCategory().isArchived());
                        return ps;
                    }, kh
            );
            final UUID generatedKeyOfCategory = (UUID) kh.getKeys().get("id");
            spend.getCategory().setId(generatedKeyOfCategory);
        }
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ? )",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    return ps;
                }, kh
        );
        final UUID generatedKeyOfSpend = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKeyOfSpend);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                          SELECT s.id, s.username, s.spend_date, s.currency, s.amount, s.description, c.id as category_id,
                                                                   c.name as category_name, c.archived as category_archived
                                                                   FROM spend s JOIN category c on s.category_id = c.id
                                                                   WHERE s.id = ?
                                """,
                        SpendEntityExtractor.instance,
                        id
                )
        );
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        categoryDAOSpringJdbc.create(spend.getCategory());
        spendDAOSpringJdbc.update(spend);
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        categoryDAOSpringJdbc.create(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return  categoryDAOSpringJdbc.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDAOSpringJdbc.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDAOSpringJdbc.findSpendById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDAOSpringJdbc.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDAOSpringJdbc.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDAOSpringJdbc.deleteCategory(category);
    }
}
