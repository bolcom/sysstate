package nl.unionsoft.sysstate.security;

import java.util.Collection;

import javax.inject.Inject;

import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    private UserLogic userLogic;

    @Inject
    public UserDetailsServiceImpl(UserLogic userLogic) {
        this.userLogic = userLogic;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        UserDto userDto = userLogic.getUserByLogin(userName);
        if (userDto == null) {
            throw new UsernameNotFoundException("Could not find User [" + userName + "] ");
        }
        // TODO Auto-generated method stub
        return new CustomUserDetails(userDto);
    }

    public static class CustomUserDetails implements UserDetails {

        private static final long serialVersionUID = 5214790461546088537L;

        private UserDto userDto;

        public CustomUserDetails(UserDto userDto) {
            this.userDto = userDto;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return AuthorityUtils.createAuthorityList("ROLE_ADMIN");
        }

        @Override
        public String getPassword() {
            return userDto.getPassword();
        }

        @Override
        public String getUsername() {
            return userDto.getLogin();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }
}
