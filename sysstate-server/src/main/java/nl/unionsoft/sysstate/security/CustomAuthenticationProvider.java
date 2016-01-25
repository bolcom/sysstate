package nl.unionsoft.sysstate.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.stereotype.Service;

@Service("customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserLogic userLogic;

    @Inject
    public CustomAuthenticationProvider(UserLogic userLogic) {
        this.userLogic = userLogic;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken upAuth = (UsernamePasswordAuthenticationToken) authentication;

            String principal = ObjectUtils.toString(upAuth.getPrincipal());
            String credentials = ObjectUtils.toString(upAuth.getCredentials());
            Optional<UserDto> optionalUserDto = userLogic.getAuthenticatedUser(principal, credentials);

            if (!optionalUserDto.isPresent()) {
                throw new BadCredentialsException("Bad Credentials");
            }
            UserDto userDto = optionalUserDto.get();

            //@formatter:off
            Collection<? extends GrantedAuthority> authorities = userDto.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());
            //@formatter:on

            return new UsernamePasswordAuthenticationToken(userDto, null, authorities);

        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

}
