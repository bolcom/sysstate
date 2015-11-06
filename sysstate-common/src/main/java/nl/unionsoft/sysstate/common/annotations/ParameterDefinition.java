package nl.unionsoft.sysstate.common.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import nl.unionsoft.common.converter.BidirectionalConverter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;

@Target(FIELD)
@Retention(RUNTIME)
public @interface ParameterDefinition {

    String title() default "";

    String description() default "";
    
    String defaultValue() default "";

    boolean nullable() default true;
    
    Class<? extends ListOfValueResolver> lovResolver() default ListOfValueResolver.class;


}
