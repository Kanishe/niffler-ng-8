package guru.qa.niffler.jupiter.annotation.meta;

import guru.qa.niffler.jupiter.extention.BrowserExtension;
import guru.qa.niffler.jupiter.extention.UsersQueueExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        UsersQueueExtension.class,
        BrowserExtension.class})
public @interface WebTest {


}
