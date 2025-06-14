package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import static guru.qa.niffler.data.DataBases.transaction;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final CategoryDAOSpringJdbc categoryDAOSpringJdbc = new CategoryDAOSpringJdbc();
    private final SpendDAOSpringJdbc spendDAOSpringJdbc = new SpendDAOSpringJdbc();


    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDAOSpringJdbc.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendDAOSpringJdbc.createSpend(spendEntity)
                    );
                }
        );
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(
                    new CategoryDAOJdbc().create(categoryEntity));
        }, CFG.spendJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public SpendJson createSpendSpringJdbc(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDAOSpringJdbc
                    .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDAOSpringJdbc
                        .createSpend(spendEntity)
        );
    }
}
