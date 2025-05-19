package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthUserDbClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.FakerGenUtil.*;

public class JDBCTest {

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
        UserJson createdUser = usersDbClient.createUserJdbcWithOutTransactions(user);
        System.out.println(createdUser);

        assertTrue(usersDbClient.isUserCreatedInUd(createdUser.id()));
        assertTrue(usersDbClient.isUserCreatedInUserAuth(createdUser.username()));
        assertTrue(usersDbClient.isAllAuthoritiesCreated(createdUser.username()));
    }
}
