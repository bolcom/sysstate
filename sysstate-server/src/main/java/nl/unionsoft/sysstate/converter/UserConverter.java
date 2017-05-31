package nl.unionsoft.sysstate.converter;

import java.util.List;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.domain.User;
import nl.unionsoft.sysstate.domain.UserRole;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.dto.UserDto.Role;

import org.springframework.stereotype.Service;

@Service("userConverter")
public class UserConverter implements Converter<UserDto, User> {

    public UserDto convert(User user) {
        UserDto result = null;
        if (user != null) {
            result = new UserDto();
            result.setConfiguration(user.getConfiguration());
            result.setEnabled(user.isEnabled());
            result.setFirstName(user.getFirstName());
            result.setId(user.getId());
            result.setLastName(user.getLastName());
            result.setLogin(user.getLogin());
            result.setToken(user.getToken());
            final List<Role> roles = result.getRoles();
            for (final UserRole userRole : user.getRoles()) {
                if (Role.isExistingRole(userRole.getAuthority())) {
                    roles.add(Role.valueOf(userRole.getAuthority()));
                }

            }
        }
        return result;
    }

}
