package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserDAO;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOSpringJdbc implements UserDAO {

    private final DataSource dataSource;

    public UserdataUserDAOSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO \"user\" (username, currency,firstname,surname,full_name,photo,photo_small) VALUES (?, ?,?,?, ?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getUsername());
                    ps.setObject(2, user.getCurrency().name());
                    ps.setString(3, user.getFirstname());
                    ps.setString(4, user.getSurname());
                    ps.setString(5, user.getFullname());
                    ps.setBytes(6, user.getPhoto());
                    ps.setBytes(7, user.getPhotoSmall());
                    return ps;
                },
                keyHolder);
        final UUID generatedKeys = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKeys);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\"  WHERE id = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                ));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public UserEntity update(UserEntity user) {
        return null;
    }

    @Override
    public void delete(UserEntity user) {

    }
}
