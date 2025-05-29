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
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
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
import static utils.FakerGenUtil.*;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    //Repository JDBC
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UserdataRepository userDataRepository = new UserdataRepositoryJdbc();

    //Repository Spring JDBC
    private final AuthUserRepository authUserRepositorySpringJdbc = new AuthUserRepositorySpringJdbc();
    private final UserdataRepository userDataRepositorySpringJdbc = new UserdataRepositorySpringJdbc();

    //Repository+JNDL
    private final AuthUserRepository authUserRepositoryHibernate = new AuthUserRepositoryHibernate();
    private final UserdataRepository userdataRepositoryHibernate = new UserdataRepositoryHibernate();

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

    public UserJson createUsingJNDI(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntityShort(username, password);

                    authUserRepositoryHibernate.create(authUser);
                    return UserJson.fromEntity(
                            userdataRepositoryHibernate.create(userEntityShort(username)),
                            null
                    );
                }
        );
    }

    public UserJson create(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(user);

                    authUserRepository.create(authUser);
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

        AuthUserEntity createdAuthUser = authUserSpringDao.create(authUser);
        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(authUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.create(authorityEntities);
        return UserJson.fromEntity(
                udUserSpringDao.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    public UserJson createUserJdbcWithoutTransactions(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    UserEntity ue = new UserEntity();
                    ue.setId(createdAuthUser.getId());
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthoritySpringDao.create(authorityEntities);
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

                    AuthUserEntity createdAuthUser = authUserSpringDao.create(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                UserEntity ue = new UserEntity();
                                ue.setId(createdAuthUser.getId());
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthoritySpringDao.create(authorityEntities);
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

                    AuthUserEntity createdAuthUser = authUserRepository.create(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                UserEntity ue = new UserEntity();
                                ue.setId(createdAuthUser.getId());
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthoritySpringDao.create(authorityEntities);

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

    //создание юзера используя jdbc и Repository
    public UserJson xaCreateUserRepository(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(user);
            authUserRepository.create(authUserEntity);


            return UserJson.fromEntity(
                    userDataRepository.create(UserEntity.fromJson(user)),
                    null
            );
        });
    }

    //создание юзера с repository Spring JDBC
    public UserJson xaCreateUserSpringRepository(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(user);

            authUserRepositorySpringJdbc.create(authUserEntity);

            return UserJson.fromEntity(
                    userDataRepositorySpringJdbc.create(UserEntity.fromJson(user)
                    ), null);
        });
    }


    public void addFriend(UserJson user, UserJson friend) {
        xaTransactionTemplate.execute(() -> {
            userDataRepository.addFriend(UserEntity.fromJson(user), UserEntity.fromJson(friend));
            return null;
        });
    }

    public void addInvitation(UserJson requester, UserJson addressee) {
        userDataRepository.addFriendshipRequest(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
    }

    //Методы для проверки работы Hibernate и JDNI
    void addFriendJDNI(UserJson user, UserJson friend) {
        xaTransactionTemplate.execute(() -> {
            userDataRepository.addFriend(UserEntity.fromJson(user), UserEntity.fromJson(friend));
            return null;
        });
    }

    public void addInvitationJDNI(UserJson requester, int count) {
        if (count > 0) {
            UserEntity targetEntity = userDataRepository.findById(requester.id()).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = genRandomPassword();
                            AuthUserEntity authUser = authUserEntityShort(username, "123456");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userDataRepository.create(userEntityShort(username));

                            userDataRepository.addFriendshipRequest(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    private AuthUserEntity authUserEntity(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("123456"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(authUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toList());
        return authUser;
    }

    private AuthUserEntity authUserEntityShort(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(authUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toList());
        return authUser;
    }

    private UserEntity userEntityShort(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

}
