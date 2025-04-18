package guru.qa.niffler.jupiter.extention;

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
    private CategoryJson created;

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnnotation -> {
                    if (ArrayUtils.isNotEmpty(userAnnotation.categories())) {
                        CategoryJson category = new CategoryJson(
                                null,
                                genRandomName(),
                                userAnnotation.username(),
                                userAnnotation.categories()[0].archived()
                        );
                        CategoryJson created = spendDbClient.addCategory(category);
                        if (userAnnotation.categories()[0].archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    created.id(),
                                    created.name(),
                                    created.username(),
                                    true
                            );
                            created = spendDbClient.updateCategory(archivedCategory);
                        }
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }


    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional.ofNullable(context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class))
                .ifPresent(categoryJson -> {
                    spendDbClient.updateCategory(
                            new CategoryJson(
                                    categoryJson.id(),
                                    categoryJson.name(),
                                    categoryJson.username(),
                                    true
                            ));
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
