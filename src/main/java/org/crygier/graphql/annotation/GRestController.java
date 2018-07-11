package org.crygier.graphql.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 为了让原本的Controller中的RestController能方便的切换到GraphQL mutation，互相切换
 */
public @interface GRestController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     * @since 4.0.1
     */
    String value() default "";

}

