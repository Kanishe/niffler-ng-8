package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static utils.FakerGenUtil.*;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private CategoryJson created;

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.categories())) {
                        CategoryJson category = new CategoryJson(
                                null,
                                genRandomName(),
                                userAnno.username(),
                                userAnno.categories()[0].archived()
                        );

                        CategoryJson created = spendDbClient.addCategory(category);

                        context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                    }
                });
    }


    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<CategoryJson> categoryJson = Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class));
        categoryJson.ifPresent(category -> {
            if (category.archived()) {
                CategoryJson archivedCategory = new CategoryJson(
                        category.id(),
                        category.name(),
                        category.username(),
                        true
                );
                spendApiClient.updateCategory(archivedCategory);
            }
        });
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
