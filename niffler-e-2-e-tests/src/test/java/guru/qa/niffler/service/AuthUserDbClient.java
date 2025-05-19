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

import javax.sql.DataSource;
import java.util.Arrays;

import static guru.qa.niffler.data.DataBases.dataSource;

public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();
    private final DataSource authDataSource = dataSource(CFG.authJdbcUrl());
    private final DataSource userDataDataSource = dataSource(CFG.userdataJdbcUrl());

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDAOSpringJdbc authUserDAO = new AuthUserDAOSpringJdbc(authDataSource);
    private final AuthAuthorityDAOSpringJdbc authAuthorityDAO = new AuthAuthorityDAOSpringJdbc(authDataSource);
    private final UserdataUserDAOSpringJdbc userdataUserDAO = new UserdataUserDAOSpringJdbc(userDataDataSource);

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(pe.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDAO.createUser(authUserEntity);
        AuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }).toArray(AuthorityEntity[]::new);


        authAuthorityDAO.createUser(userAuthorities);
        return UserJson.fromEntity(userdataUserDAO.createUser(
                UserEntity.fromJson(user)

        ), null);
    }

}
