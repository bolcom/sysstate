package nl.unionsoft.sysstate.security;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.unionsoft.sysstate.common.Constants;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.util.NestedServletException;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserLogic userLogic;

    @Inject
    public TokenAuthenticationFilter(UserLogic userLogic) {
        super("/");
        this.userLogic = userLogic;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = request.getHeader(Constants.SECURITY_TOKEN_HEADER);

        if (StringUtils.isEmpty(token)) {
            chain.doFilter(request, response);// return to others spring security filters
            return;
        }

        Authentication authResult;
        try {
            authResult = attemptAuthentication(request, response, token);
            if (authResult == null) {
                notAuthenticated(request, response, new LockedException("User Not found"));
                return;
            }
        } catch (AuthenticationException failed) {
            notAuthenticated(request, response, failed);
            return;
        }

        try {
            successfulAuthentication(request, response, chain, authResult);
            return;
        } catch (NestedServletException e) {
            logger.error(e.getMessage(), e);
            if (e.getCause() instanceof AccessDeniedException) {
                notAuthenticated(request, response, new LockedException("Forbidden"));
                return;
            }
        }
    }

    public void notAuthenticated(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, String token) throws AuthenticationException,
            IOException, ServletException {

        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(token);
        if (userAuthenticationToken == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        return userAuthenticationToken;
    }

    private AbstractAuthenticationToken authUserByToken(String tokenRaw) {

        AbstractAuthenticationToken authToken = null;
        try {
            logger.info("token received = " + tokenRaw);
            Optional<UserDto> optUserDto = userLogic.getAuthenticatedUser(tokenRaw);
            if (optUserDto.isPresent()) {
                UserDto userDto = optUserDto.get();
                return new PreAuthenticatedAuthenticationToken(userDto, null, SecurityUtil.getGrantedAuthorities(userDto));
            }
        } catch (Exception e) {
            logger.error("Error during authUserByToken", e);
        }
        return authToken;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        request.getRequestDispatcher(request.getRequestURI()).forward(request, response);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException,
            ServletException {

        logger.error("No TOKEN PROVIDED");
        return null;
    }

}
