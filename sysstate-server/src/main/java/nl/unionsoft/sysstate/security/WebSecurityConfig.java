package nl.unionsoft.sysstate.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/", "/template/render/**", "/images/**", "/css/**", "/js/**", "/materialize/**", "/scripts/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("password").roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().hasRole("USER").and()
        .formLogin().loginPage("/login.html").permitAll();
    }

    // @Configuration
    // @Order(1)
    // public static class FormWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    // public void configure(WebSecurity web) throws Exception {
    //
    // }
    //
    // protected void configure(HttpSecurity http) throws Exception {
    // http.authorizeRequests().anyRequest().hasRole("USER").and().formLogin().loginPage("/login.html").usernameParameter("username")
    // .passwordParameter("password").permitAll();
    // }
    //
    // protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    //
    //
    // auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
    // auth.inMemoryAuthentication().withUser("admin").password("password").roles("USER", "ADMIN");
    //
    // }
    //
    // }

    // @Configuration
    // @Order(2)
    // public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    // protected void configure(HttpSecurity http) throws Exception {
    // http.antMatcher("/api/**").httpBasic();
    // }
    //
    // protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    // auth.inMemoryAuthentication()
    // .withUser("user").password("password").roles("USER").and()
    // .withUser("admin").password("password").roles("USER", "ADMIN");
    // }
    // }

}
