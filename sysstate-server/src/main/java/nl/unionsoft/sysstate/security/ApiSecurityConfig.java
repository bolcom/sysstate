package nl.unionsoft.sysstate.security;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(1)
@Configuration
@EnableWebSecurity
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    @Named("usernameAndPasswordAuthenticationProvider")
    private AuthenticationProvider usernameAndPasswordAuthenticationProvider;

    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .authorizeRequests()
                .antMatchers(toApiPaths("/instance/**"))
                .hasAnyRole("EDITOR", "ADMIN").and()
           .authorizeRequests()
                .antMatchers(toApiPaths("/scheduler/**"))
                .hasAnyRole("ADMIN").and()
           .authorizeRequests()
               .antMatchers(HttpMethod.GET, "/view/**")
               .hasAnyRole("ANONYMOUS", "EDITOR", "ADMIN" ).and()
            .httpBasic();
        //@formatter:on
    }
    

    private String[] toApiPaths(String path)
    {
        return new String[] {"/api" + path, "/services" + path};
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernameAndPasswordAuthenticationProvider);
    }
}
