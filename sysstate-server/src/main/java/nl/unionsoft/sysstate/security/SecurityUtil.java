package nl.unionsoft.sysstate.security;

import java.util.Collection;
import java.util.stream.Collectors;

import nl.unionsoft.sysstate.dto.UserDto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtil {

    public static Collection<? extends GrantedAuthority> getGrantedAuthorities(UserDto userDto) {
        //@formatter:off
         return userDto.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
         //@formatter:on
    }
}
