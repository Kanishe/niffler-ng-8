package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity createUser(AuthUserEntity user);

    Optional<AuthUserEntity> findById(UUID id);

    Optional<AuthUserEntity> findByUsername(String username);
}
