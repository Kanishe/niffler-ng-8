package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO {

    private static final Config CFG = Config.getInstance();


    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO \"user\" (username, password,enabled,account_non_locked,account_non_expired,credentials_non_expired) VALUES (?, ?,?,?, ?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getPassword());
                    ps.setBoolean(3, user.getEnabled());
                    ps.setBoolean(4, user.getAccountNonLocked());
                    ps.setBoolean(5, user.getAccountNonExpired());
                    ps.setBoolean(6, user.getCredentialsNonExpired());
                    return ps;
                },
                keyHolder);
        final UUID generatedKeys = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKeys);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\"  WHERE id = ?",
                        AuthUserEntityRowMapper.instance,
                        id
                ));
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }
}
