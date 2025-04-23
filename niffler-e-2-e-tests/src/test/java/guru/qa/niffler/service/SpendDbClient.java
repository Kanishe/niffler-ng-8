package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.DataBases.transaction;

public class SpendDbClient {

    private final static Config CONFIG = Config.getInstance();
//    private final SpendDAO spendDao = new SpendDAOJdbc();
//    private final CategoryDAO categoryDao = new CategoryDAOJdbc();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDAOJdbc(connection)
                                .createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDAOJdbc(connection).createSpend(spendEntity)
                    );
                },
                CONFIG.spendJdbcUrl());
    }

    public CategoryJson addCategory(CategoryJson category) {
        return null;
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return null;
    }

    public Optional<SpendEntity> findById(UUID id) {
        return null;
    }

    public List<SpendEntity> findAllByUsername(String username) {
        return null;
    }

    public void deleteSpendById(SpendEntity spend) {

    }

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return null;
    }

    public List<CategoryEntity> findCategoryByUserName(String categoryUserName) {
        return null;
    }

    public void deleteCategory(CategoryEntity category) {
    }
}
