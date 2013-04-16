package nl.unionsoft.sysstate.converter;

import java.util.List;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.domain.User;
import nl.unionsoft.sysstate.domain.UserRole;
import nl.unionsoft.sysstate.dto.UserDto;

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
            final List<String> roles = result.getRoles();
            for (final UserRole userRole : user.getRoles()) {
                roles.add(userRole.getAuthority());
            }
        }
        return result;
    }

}
