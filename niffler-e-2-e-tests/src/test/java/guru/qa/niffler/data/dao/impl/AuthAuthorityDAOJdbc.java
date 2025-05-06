package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDAOJdbc implements AuthAuthorityDAO {

    private final Connection connection;

    public AuthAuthorityDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity create(AuthorityEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) " +
                        "VALUES (?, ? )",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, user.getUserId());
            ps.setString(2, user.getAuthority().name());
            ps.executeUpdate();
            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Cant find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
