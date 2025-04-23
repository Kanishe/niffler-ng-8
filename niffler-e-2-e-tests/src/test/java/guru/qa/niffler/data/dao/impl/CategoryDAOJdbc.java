package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.entity.category.CategoryEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDAOJdbc implements CategoryDAO {

    private static final Config CONFIG = Config.getInstance();

    private final Connection connection;

    public CategoryDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
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

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ? AND name = ?")) {
            ps.setString(1, username);
            ps.setString(2, categoryName);

            ps.execute();
            try (ResultSet resultSet = ps.getResultSet()) {
                if (resultSet.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(resultSet.getObject("id", UUID.class));
                    categoryEntity.setName(resultSet.getString("name"));
                    categoryEntity.setUsername(resultSet.getString("username"));
                    categoryEntity.setArchived(resultSet.getBoolean("archived"));

                    return Optional.of(categoryEntity);
                } else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ?")) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet resultSet = ps.getResultSet()) {
                List<CategoryEntity> categoryEntityList = new ArrayList<>();

                while (resultSet.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(resultSet.getObject("id", UUID.class));
                    categoryEntity.setName(resultSet.getString("name"));
                    categoryEntity.setUsername(resultSet.getString("username"));
                    categoryEntity.setArchived(resultSet.getBoolean("archived"));

                    categoryEntityList.add(categoryEntity);
                }
                return categoryEntityList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CategoryEntity update(CategoryEntity category) {
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category id must not be null");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
        )) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.getArchived());
            ps.setObject(4, category.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Could not update category");
            }

            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(CategoryEntity category) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM category WHERE id = ?")) {
            ps.setObject(1, category.getId());
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new SQLException("Could not delete category");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
