package app.simplecloud.simplecloud.module.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.10.22
 * Time: 14:32
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Module {

    String name();

    String author() default "<>";

    String[] depend() default {};

    String[] softDepend() default {};

}
