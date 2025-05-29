package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.SpendRepositorySpringJdbc;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthUserDbClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FakerGenUtil.*;

public class JDBCTest {

    @Test
    void springJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.create(
                new UserJson(
                        null,
                        genRandomName(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void authSpringJdbcTest() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();
        UserJson user = authUserDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        genRandomName(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createSpendJdbcTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spendJson = spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                genRandomName(),
                                genRandomCommerceName(),
                                false
                        ),
                        CurrencyValues.RUB,
                        1111.00,
                        genRandomName(),
                        genRandomName()
                )
        );
        System.out.println(spendJson);
    }

    @Test
    void createUserSpringWithOutTx() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserJson user = new UserJson(
                null,
                genRandomName(),
                null,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                null
        );
        UserJson createdUser = usersDbClient.createUserSpringWithOutTransactions(user);
        System.out.println(createdUser);

        assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
        assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
        assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
    }


    @Test
    void createUserWithSpringChainedTxManager() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserJson user = new UserJson(
                null,
                genRandomName(),
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null
        );
        UserJson createdUser = usersDbClient.createUserChainedTxManager(user);
        System.out.println(createdUser);

        assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
        assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
        assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
    }

    @Test
    void rollBackCreateUserWithSpringChainedTxManager() {

        UsersDbClient usersDbClient = new UsersDbClient();

        UserJson user = new UserJson(
                null,
                genRandomName(),
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null
        );

        System.out.println(user);
        usersDbClient.createUserChainedTxManager(user);
    }

    @Test
    void createUserJdbcWithTx() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserJson user = new UserJson(
                null,
                genRandomName(),
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null
        );
        UserJson createdUser = usersDbClient.createUserJdbcWithXaTransactions(user);
        System.out.println(createdUser);

        assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
        assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
        assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
    }

    @Test
    void createUserJdbcWithOutTx() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserJson user = new UserJson(
                null,
                genRandomName(),
                null,
                null,
                null,
                CurrencyValues.EUR,
                null,
                null,
                null
        );
        UserJson createdUser = usersDbClient.createUserJdbcWithoutTransactions(user);
        System.out.println(createdUser);

        assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
        assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
        assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
    }

    @Test
    void spendRepositoryJdbcTest() {
        SpendRepositoryJdbc spendRepositoryJdbc = new SpendRepositoryJdbc();
        SpendEntity spend = SpendEntity.fromJson(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                genRandomName(),
                                genRandomCommerceName(),
                                false
                        ),
                        CurrencyValues.RUB,
                        100.0,
                        genRandomCommerceName(),
                        genRandomEmail()
                )
        );
        SpendEntity spendEntity = spendRepositoryJdbc.create(spend);

        Optional<SpendEntity> spendById = spendRepositoryJdbc.findSpendById(spendEntity.getId());
        SpendJson spendJsonById = SpendJson.fromEntity(spendById.orElseThrow());
        System.out.println(spendJsonById);
    }

    @Test
    void spendRepositorySpringJdbc() {
        SpendRepositorySpringJdbc spendRepositoryJdbc = new SpendRepositorySpringJdbc();
        SpendEntity spend = SpendEntity.fromJson(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                genRandomName(),
                                genRandomCommerceName(),
                                false
                        ),
                        CurrencyValues.RUB,
                        100.0,
                        genRandomName(),
                        genRandomCommerceName()
                )
        );
        SpendEntity spendEntity = spendRepositoryJdbc.create(spend);

        Optional<SpendEntity> spendById = spendRepositoryJdbc.findSpendById(spendEntity.getId());
        SpendJson spendJsonById = SpendJson.fromEntity(spendById.orElseThrow());
        System.out.println(spendJsonById);
    }

    //добавление друзей пользователей со связью в
    @Test
    void addFriendsWithFriendship() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson firstFriend = usersDbClient.xaCreateUserRepository(
                new UserJson(
                        null,
                        genRandomPassword() + "firstFriend",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );

        UserJson secondFriend = usersDbClient.xaCreateUserRepository(
                new UserJson(
                        null,
                        genRandomPassword() + "secondFriend ",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );

        usersDbClient.addFriend(firstFriend, secondFriend);
    }

    @Test
    void addFriendsWithInvitation() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson firstFriend = usersDbClient.xaCreateUserRepository(
                new UserJson(
                        null,
                        genRandomPassword() + "firstFriend",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );

        UserJson secondFriend = usersDbClient.xaCreateUserRepository(
                new UserJson(
                        null,
                        genRandomPassword() + "secondFriend ",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );

        usersDbClient.addInvitation(firstFriend, secondFriend);
    }

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valera10"
    })
    @ParameterizedTest
    void springJdbcTestJNDI() {
        UserJson user = usersDbClient.createUsingJNDI(
                genRandomName(),
                genRandomPassword()
        );
        usersDbClient.addInvitationJDNI(user, 1);
        System.out.println(user);
    }

    @ValueSource(strings = {
            "xaCreateUserHibernateRepository17",
    })
    @ParameterizedTest
    void xaCreateUserHibernateRepository(String username) {
        UsersDbClient userDbClient = new UsersDbClient();

        UserJson user = userDbClient.xaCreateUserHibernateRepository(
                username, "12345"
        );
        userDbClient.addIncomeInvitation(user, 1);
        userDbClient.addOutcomeInvitation(user, 1);
    }
}
