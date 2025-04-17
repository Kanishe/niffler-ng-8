package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.category.CategoryEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {
    private static final Config CONFIG = Config.getInstance();

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (Connection connection = DataBases.getConnection(CONFIG.spendJdbcUrl())) {
            try (PreparedStatement prepareStatement = connection
                    .prepareStatement(
                            "INSERT INTO category (name, username,archived) VALUES (?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS)) {
//                prepareStatement.setObject(1, category.getId());
                prepareStatement.setString(1, category.getName());
                prepareStatement.setString(2, category.getUsername());
                prepareStatement.setBoolean(3, category.getArchived());
                prepareStatement.executeUpdate();

                prepareStatement.getResultSet();

                final UUID generatedId;
                try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedId = resultSet.getObject("id", UUID.class);
                    } else throw new SQLException("Could not get generated ID");
                }
                category.setId(generatedId);
                return category;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (Connection connection = DataBases.getConnection(CONFIG.spendJdbcUrl())) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM category WHERE id = ?"
            )) {
                preparedStatement.setObject(1, id);

                preparedStatement.execute();
                preparedStatement.getResultSet();

                try (ResultSet resultSet = preparedStatement.getResultSet()) {
                    if (resultSet.next()) {
                        CategoryEntity categoryEntity = new CategoryEntity();
                        categoryEntity.setId(resultSet.getObject("id", UUID.class));
                        categoryEntity.setName(resultSet.getString("name"));
                        categoryEntity.setUsername(resultSet.getString("username"));
                        categoryEntity.setArchived(resultSet.getBoolean("archived"));
                        return Optional.of(
                                categoryEntity
                        );
                    } else return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
