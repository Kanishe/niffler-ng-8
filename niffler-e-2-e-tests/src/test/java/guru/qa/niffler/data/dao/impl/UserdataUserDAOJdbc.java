package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDAO;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDAOJdbc implements UserDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        try (PreparedStatement pt = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        UPDATE \"user\"
                        SET
                        currency = ?,
                        firstname = ?,
                        surname = ?,
                        photo = ?,
                        photo_small = ?
                        WHERE id = ?
                        """
        ); PreparedStatement friendsPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        INSERT INTO friendship (requester_id, addressee_id, status)
                        VALUES (?, ?, ?)
                        """)
        ) {
            userPs.setString(1, user.getCurrency().name());
            userPs.setString(2, user.getFirstname());
            userPs.setString(3, user.getSurname());
            userPs.setBytes(4, user.getPhoto());
            userPs.setBytes(5, user.getPhotoSmall());
            userPs.setObject(6, user.getId());
            userPs.executeUpdate();

            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                friendsPs.setObject(1, user.getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setString(3, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }
            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
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

    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            List<UserEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    UserEntity ue = new UserEntity();

                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname((rs.getString("firstname")));
                    ue.setSurname((rs.getString("surname")));
                    ue.setFullname((rs.getString("full_name")));
                    ue.setPhoto((rs.getBytes("photo_small")));
                    ue.setPhotoSmall((rs.getBytes("photoSmall")));
                    result.add(ue);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
