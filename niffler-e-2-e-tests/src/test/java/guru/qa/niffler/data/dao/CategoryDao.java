package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.category.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity createCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);
}
