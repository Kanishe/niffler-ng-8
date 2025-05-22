package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.dao.UserDAO;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    private final AuthUserDAO authUserSpringDao = new AuthUserDAOSpringJdbc();
    private final AuthAuthorityDAO authAuthoritySpringDao = new AuthAuthorityDAOSpringJdbc();
    private final UserDAO udUserSpringDao = new UserdataUserDAOSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserSpringDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                UserEntity ue = new UserEntity();
                                ue.setId(createdAuthUser.getId());
                                ae.setUser(ue);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthoritySpringDao.createUser(authorityEntities);
                    return UserJson.fromEntity(
                            udUserSpringDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        );
    }

    public UserJson createUserSpringWithOutTransactions(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDao.createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    UserEntity ue = new UserEntity();
                    ue.setId(createdAuthUser.getId());
                    ae.setUser(ue);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.createUser(authorityEntities);
        return UserJson.fromEntity(
                udUserSpringDao.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    public UserJson createUserJdbcWithOutTransactions(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDao.createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    UserEntity ue = new UserEntity();
                    ue.setId(createdAuthUser.getId());
                    ae.setUser(ue);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.createUser(authorityEntities);
        return UserJson.fromEntity(
                udUserSpringDao.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    public UserJson createUserJdbcWithXaTransactions(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserSpringDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                UserEntity ue = new UserEntity();
                                ue.setId(createdAuthUser.getId());
                                ae.setUser(ue);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthoritySpringDao.createUser(authorityEntities);
                    return UserJson.fromEntity(
                            udUserSpringDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        );
    }

    public UserJson createUserChainedTxManager(UserJson user) {
        return txTemplate.execute((status) -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserSpringDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                UserEntity ue = new UserEntity();
                                ue.setId(createdAuthUser.getId());
                                ae.setUser(ue);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthoritySpringDao.createUser(authorityEntities);

                    UserEntity createdUser = udUserSpringDao.createUser(UserEntity.fromJson(user));

                    return UserJson.fromEntity(createdUser, null);
                }
        );
    }

    public boolean isUserCreatedInUd(UUID id) {
        Optional<UserEntity> userEntity = udUserSpringDao.findById(id);
        return userEntity.isPresent();
    }

    public boolean isUserCreatedInUserAuth(String username) {
        List<AuthUserEntity> allList = authUserSpringDao.findAll();
        Optional<AuthUserEntity> authUserEntity =
                allList.stream()
                        .filter(aue -> aue.getUsername().equals(username))
                        .findFirst();
        return authUserEntity.isPresent();
    }

    public boolean isAllAuthoritiesCreated(String username) {
        List<AuthUserEntity> aueList = authUserSpringDao.findAll();
        AtomicInteger count = new AtomicInteger();
        aueList.stream()
                .filter(aue -> aue.getUsername().equals(username))
                .findFirst()
                .ifPresent(
                        (authUserEntity) -> {
                            List<AuthorityEntity> allList = authAuthoritySpringDao.findAll();
                            count.set((int) allList.stream()
                                    .filter(authority -> authority.getUser().getId()
                                            .equals(authUserEntity.getId()))
                                    .count());
                        }
                );
        return count.get() == Authority.values().length;
    }

}
