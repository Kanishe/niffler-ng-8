package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDAOSpringJdbc authUserDAOSpringJdbc = new AuthUserDAOSpringJdbc();
    private final AuthAuthorityDAOSpringJdbc authAuthorityDAOSpringJdbc = new AuthAuthorityDAOSpringJdbc();
    private final UserdataUserDAOSpringJdbc userdataUserDAOSpringJdbc = new UserdataUserDAOSpringJdbc();


    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(pe.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDAOSpringJdbc.create(authUserEntity);
        AuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }).toArray(AuthorityEntity[]::new);

        authAuthorityDAOSpringJdbc.create(userAuthorities);
        return UserJson.fromEntity(userdataUserDAOSpringJdbc.createUser(
                        UserEntity.fromJson(user)
                ),
                null);
    }

}
