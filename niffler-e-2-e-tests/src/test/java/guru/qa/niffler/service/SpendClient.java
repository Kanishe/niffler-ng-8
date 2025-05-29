package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public interface SpendClient {

    public SpendJson createSpend(SpendJson spend);
    public CategoryJson createCategory(CategoryJson categoryJson);
}
