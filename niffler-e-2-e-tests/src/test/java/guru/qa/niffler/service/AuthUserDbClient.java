package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(pe.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDAOSpringJdbc().createUser(authUserEntity);
        AuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }).toArray(AuthorityEntity[]::new);


        new AuthAuthorityDAOSpringJdbc().createUser(userAuthorities);
        return UserJson.fromEntity(new UserdataUserDAOSpringJdbc().createUser(
                UserEntity.fromJson(user)
        ), null);
    }

}
