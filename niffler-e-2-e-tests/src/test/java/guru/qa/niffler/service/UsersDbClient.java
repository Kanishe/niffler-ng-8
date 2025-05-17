package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.userAuth.AuthUserEntity;
import guru.qa.niffler.data.entity.userAuth.Authority;
import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.data.DataBases.dataSource;
import static guru.qa.niffler.data.DataBases.xaTransaction;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDAOSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDAOSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createUser(authorityEntities);

        return UserJson.fromEntity(
                new UserdataUserDAOSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .createUser(UserEntity.fromJson(user)
                        )
        );
    }


    public UserJson createUser(UserJson user) {
        return UserJson.fromEntity(
                xaTransaction(
                        new DataBases.XaFunction<>(
                                con -> {
                                    AuthUserEntity authUser = new AuthUserEntity();
                                    authUser.setUsername(user.username());
                                    authUser.setPassword(pe.encode("12345"));
                                    authUser.setEnabled(true);
                                    authUser.setAccountNonExpired(true);
                                    authUser.setAccountNonLocked(true);
                                    authUser.setCredentialsNonExpired(true);
                                    new AuthUserDAOJdbc(con).createUser(authUser);
                                    new AuthAuthorityDAOJdbc(con).createUser(
                                            Arrays.stream(Authority.values())
                                                    .map(a -> {
                                                                AuthorityEntity ae = new AuthorityEntity();
                                                                ae.setUserId(authUser.getId());
                                                                ae.setAuthority(a);
                                                                return ae;
                                                            }
                                                    ).toArray(AuthorityEntity[]::new));
                                    return null;
                                },
                                CFG.authJdbcUrl()
                        ),
                        new DataBases.XaFunction<>(
                                con -> {
                                    UserEntity ue = new UserEntity();
                                    ue.setUsername(user.username());
                                    ue.setFullname(user.fullname());
                                    ue.setCurrency(user.currency());
                                    new UserdataUserDAOJdbc(con).createUser(ue);
                                    return ue;
                                },
                                CFG.userdataJdbcUrl()
                        )
                ));
    }

}
