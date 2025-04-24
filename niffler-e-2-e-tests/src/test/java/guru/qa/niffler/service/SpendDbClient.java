package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import static guru.qa.niffler.data.DataBases.transaction;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDAOJdbc(connection)
                        .create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(
                    new SpendDAOJdbc(connection).createSpend(spendEntity)
            );
        }, CFG.spendJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(
                    new CategoryDAOJdbc(connection).create(categoryEntity));
        }, CFG.spendJdbcUrl(), TRANSACTION_READ_COMMITTED);
    }
}
