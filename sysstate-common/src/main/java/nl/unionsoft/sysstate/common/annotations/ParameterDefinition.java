package nl.unionsoft.sysstate.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ParameterDefinition {

    String title();

    String description() default "";

    boolean nullable() default true;

    Class<ListOfValueResolver> resolver() default ListOfValueResolver.class;

}
