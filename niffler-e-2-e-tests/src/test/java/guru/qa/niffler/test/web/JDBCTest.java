package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static utils.FakerGenUtil.*;

public class JDBCTest {

    @Test
    public void testConnection() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
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
                        1000.3,
                        genRandomName(),
                        null
                )
        );
        System.err.println(spend);
    }
}
