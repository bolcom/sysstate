package nl.unionsoft.sysstate.security;

import static nl.unionsoft.sysstate.dto.UserDto.Role.ADMIN;
import static nl.unionsoft.sysstate.dto.UserDto.Role.EDITOR;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.logic.UserLogic;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Order(30)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    @Named("usernameAndPasswordAuthenticationProvider")
    private AuthenticationProvider usernameAndPasswordAuthenticationProvider;

//    @Inject
//    @Named("tokenAuthenticationFilter")
//    private TokenAuthenticationFilter tokenAuthenticationFilter;
//    
    @Inject
    @Named("userLogic")
    private UserLogic userLogic;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        //@formatter:off
        web.ignoring().antMatchers(HttpMethod.GET,
                "/","/index*",
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

        configureWeb(http);
        configureApi(http);

    }

    public void configureWeb(HttpSecurity http) throws Exception {
        //@formatter:off
        http
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, 
                "/account/**"
                ).authenticated()        
            .antMatchers(HttpMethod.GET, 
                "/projectEnvironment/project/*/environment/*/details*",
                "/filter/**",                
                "/dashboard/**",
                "/project/index*",
                "/instance/**/details*",
                "/instance/**/message*",
                "/environment/index*",                                
                "/view/index*",
                "/view/**/index*",                                  
                "/logout*", 
                "/login*",
                "/scheduler/**"
                ).permitAll()
            .antMatchers(HttpMethod.POST,
                "/manager/search*",
                "/filter/index*"                    
                ).permitAll()
            .antMatchers(
                "/environment/**",
                "/text/**",
                "/project/**",
                "/view/**",  
                "/settings/**",  
                "/template/**",
                "/filter/**",
                "/projectenvironment/**",
                "/instance/**"  
                ).hasAnyRole(ADMIN.name(),EDITOR.name())
            .antMatchers(
                "/configuration/**",
                "/cache/**",
                "/user/**",
                "/plugins/**",  
                "/script/**",
                "/scheduler/**"
                ).hasAnyRole(ADMIN.name())
            .and()            
        .formLogin()
            .loginPage("/login.html")
            .permitAll()
            .and()
        .logout()
            .logoutUrl("/logout.html")
            .logoutSuccessUrl("/dashboard/index.html")
            .permitAll();
        //@formatter:on
    }


    public void configureApi(HttpSecurity http) throws Exception {
        //@formatter:off
        http
        .csrf()
            .ignoringAntMatchers(toApiPaths("/**"))
            .and()
        .authorizeRequests()
            .antMatchers(toApiPaths("/instance/**")).hasAnyRole(EDITOR.name(), ADMIN.name())
            .antMatchers(toApiPaths("/project/**")).hasAnyRole(EDITOR.name(), ADMIN.name())
            .antMatchers(toApiPaths("/environment/**")).hasAnyRole(EDITOR.name(), ADMIN.name())
            .antMatchers(toApiPaths("/scheduler")).permitAll()
            .antMatchers(toApiPaths("/view/**")).permitAll()
            .and()
            .addFilterBefore(new TokenAuthenticationFilter(userLogic), AnonymousAuthenticationFilter.class );
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
