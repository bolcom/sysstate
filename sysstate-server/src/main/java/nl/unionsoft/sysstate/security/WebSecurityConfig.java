package nl.unionsoft.sysstate.security;

import static nl.unionsoft.sysstate.dto.UserDto.Role.ADMIN;
import static nl.unionsoft.sysstate.dto.UserDto.Role.ANONYMOUS;
import static nl.unionsoft.sysstate.dto.UserDto.Role.EDITOR;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Order(2)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    @Named("usernameAndPasswordAuthenticationProvider")
    private AuthenticationProvider usernameAndPasswordAuthenticationProvider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        //@formatter:off
        web.ignoring().antMatchers(HttpMethod.GET,
                "/","/index",
                "/template/render/**", 
                "/images/**", 
                "/css/**", 
                "/js/**",
                "/materialize/**", 
                "/scripts/**"
                );
        //@formatter:on

    }


    protected void configure(HttpSecurity http) throws Exception {
        authorizeWeb(http);
        authorizeApi(http);

    }


    public void authorizeWeb(HttpSecurity http) throws Exception {
        //@formatter:off
        http
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, 
                "/filter/**",
                "/dashboard/**",
                "/view/index*",
                "/logout*", 
                "/login*"                 
                )
            .permitAll().and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST,
                "/manager/search*",
                "/filter/index*"                    
                )
            .hasAnyRole(ANONYMOUS.name(),ADMIN.name(),EDITOR.name()).and()
        .authorizeRequests()
            .antMatchers(
                "/environment/**",
                "/text/**",
                "/project/**",
                "/view/**",  
                "/template/**",
                "/filter/**",
                "/projectenvironment/**"
                )
            .hasAnyRole(ADMIN.name(),EDITOR.name()).and()
        .authorizeRequests()
            .anyRequest()
            .hasRole(ADMIN.name()).and()
        .formLogin()
            .loginPage("/login.html")
            .permitAll().and()
        .logout()
            .logoutUrl("/logout.html")
            .logoutSuccessUrl("/dashboard/index.html")
            .permitAll();
        //@formatter:on
    }


    public void authorizeApi(HttpSecurity http) throws Exception {
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
        .csrf()
            .ignoringAntMatchers(toApiPaths("/**")).and()            
        .httpBasic();
        //@formatter:on
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernameAndPasswordAuthenticationProvider);
    }

    private String[] toApiPaths(String path)
    {
        return new String[] {"/api" + path, "/services" + path};
    }
}
