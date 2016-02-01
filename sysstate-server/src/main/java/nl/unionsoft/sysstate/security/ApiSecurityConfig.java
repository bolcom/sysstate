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
import static nl.unionsoft.sysstate.dto.UserDto.Role.ADMIN;
import static nl.unionsoft.sysstate.dto.UserDto.Role.ANONYMOUS;
import static nl.unionsoft.sysstate.dto.UserDto.Role.EDITOR;
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
                .hasAnyRole(EDITOR.name(), ADMIN.name()).and()
            .authorizeRequests()
                .antMatchers(toApiPaths("/project/**"))
                .hasAnyRole(EDITOR.name(), ADMIN.name()).and()
           .authorizeRequests()
                .antMatchers(toApiPaths("/scheduler/**"))
                .hasAnyRole(ADMIN.name()).and()
           .authorizeRequests()
               .antMatchers(HttpMethod.GET, "/view/**")
               .hasAnyRole(ANONYMOUS.name(), EDITOR.name(), ADMIN.name() ).and()
            .csrf().ignoringAntMatchers(toApiPaths("/**")).and()
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
