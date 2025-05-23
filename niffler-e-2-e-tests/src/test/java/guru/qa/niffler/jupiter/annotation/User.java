package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extention.CategoryExtension;
import guru.qa.niffler.jupiter.extention.SpendExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({CategoryExtension.class, SpendExtension.class})
public @interface User {

    String username() default "";
    Category[] categories() default {};
    Spending[] spendings() default {};

}
