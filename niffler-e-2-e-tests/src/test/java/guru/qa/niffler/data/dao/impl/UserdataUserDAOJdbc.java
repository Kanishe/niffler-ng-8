package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDAO;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOJdbc implements UserDAO {

    private static final Config CONFIG = Config.getInstance();

    private final Connection connection;

    public UserdataUserDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        try (PreparedStatement pt = connection.prepareStatement(
                "INSERT INTO user (username,currency,firstname,surname,photo, photo_small,full_name) " +
                        "VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            pt.setString(1, user.getUsername());
            pt.setString(2, user.getCurrency().name());
            pt.setString(3, user.getFirstname());
            pt.setString(4, user.getSurname());
            pt.setBytes(5, user.getPhoto());
            pt.setBytes(6, user.getPhotoSmall());
            pt.setString(7, user.getFullname());

            pt.executeUpdate();
            UUID generatedId;
            try (ResultSet generatedKeys = pt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = UUID.fromString(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Could not get generated ID");
                }
                user.setId(generatedId);
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    UserEntity user = new UserEntity();

                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    user.setFirstname(rs.getString("firstname"));
                    user.setSurname(rs.getString("surname"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhoto(rs.getBytes("photo_small"));
                    user.setFullname(rs.getString("full_name"));

                    return Optional.of(user);

                } else return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE user SET username=?, currency=?,firstname=?,surname=?," +
                        "photo=?,photo_small=?,full_name=?" +
                        " WHERE userid=? ")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            int upd = ps.executeUpdate();

            if (upd == 0) {
                throw new RuntimeException("Could not update user");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM user WHERE userid=?")) {
            ps.setObject(1, user.getId());
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("Could not delete user");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
