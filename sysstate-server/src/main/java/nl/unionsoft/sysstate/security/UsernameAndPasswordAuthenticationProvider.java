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

@Service("usernameAndPasswordAuthenticationProvider")
public class UsernameAndPasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserLogic userLogic;

    @Inject
    public UsernameAndPasswordAuthenticationProvider(UserLogic userLogic) {
        this.userLogic = userLogic;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String principal = ObjectUtils.toString(authentication.getPrincipal());
        String credentials = ObjectUtils.toString(authentication.getCredentials());
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

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

}
