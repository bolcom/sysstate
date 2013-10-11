package nl.unionsoft.sysstate.common.extending;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface ConfiguredBy {
    Class<? extends InstanceConfiguration> instanceConfig() default InstanceConfiguration.class;
    Class<? extends GlobalConfiguration> globalConfig() default GlobalConfiguration.class;

}
