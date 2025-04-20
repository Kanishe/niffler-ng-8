package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDAO spendDao = new SpendDAOJdbc();
    private final CategoryDAO categoryDao = new CategoryDAOJdbc();

    public SpendJson createSpend(SpendJson spend) {
        CategoryEntity categoryEntity;
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            Optional<CategoryEntity> optionalCategoryEntity = categoryDao.findCategoryByUsernameAndCategoryName(spendEntity.getUsername(), spendEntity.getCategory().getName());
            categoryEntity = optionalCategoryEntity.orElseGet(() -> categoryDao.createCategory(spendEntity.getCategory()));
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(spendDao.createSpend(spendEntity));
    }

    public CategoryJson addCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(categoryDao.createCategory(categoryEntity));
    }

    public CategoryJson updateCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return CategoryJson.fromEntity(categoryDao.update(categoryEntity));
    }

    public Optional<SpendEntity> findById(UUID id) {

        return spendDao.findSpendById(id);
    }

    public List<SpendEntity> findAllByUsername(String username) {
        return spendDao.findAllByUsername(username);
    }

    public void deleteSpendById(SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    public List<CategoryEntity> findCategoryByUserName(String categoryUserName) {
        return categoryDao.findAllByUsername(categoryUserName);
    }

    public void deleteCategory(CategoryEntity category) {
        categoryDao.delete(category);
    }
}
