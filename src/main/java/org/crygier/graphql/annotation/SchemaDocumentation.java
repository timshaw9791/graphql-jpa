package org.crygier.graphql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { TYPE, FIELD,METHOD})
@Retention(RUNTIME)
public @interface SchemaDocumentation {

    String value();

}
