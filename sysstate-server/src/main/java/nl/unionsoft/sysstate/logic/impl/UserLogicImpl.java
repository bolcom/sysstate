package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.dao.UserDao;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.dto.UserDto.Role;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("userLogic")
public class UserLogicImpl implements UserLogic {

    @Inject
    @Named("userDao")
    private UserDao userDao;

    public Optional<UserDto> getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDto) {
            return Optional.of((UserDto) principal);
        }
        return Optional.empty();
    }

    public List<UserDto> getUsers() {
        return userDao.getUsers();
    }

    public void createOrUpdate(final UserDto user) {
        userDao.createOrUpdate(user);

    }

    public UserDto getUser(final Long userId) {
        return userDao.getUser(userId);
    }

    public void delete(final Long userId) {
        final Optional<UserDto> currentUser = getCurrentUser();
        if (currentUser.isPresent()) {
            if (userId.equals(currentUser.get().getId())) {
                throw new IllegalStateException("You can't delete yourself!");
            }
        }
        userDao.delete(userId);

    }

    @PostConstruct
    public void addSystemUser() throws Exception {
        List<UserDto> users = userDao.getUsers();
        if (users == null || users.size() == 0) {
            UserDto defaultUser = new UserDto();
            defaultUser.setFirstName("System");
            defaultUser.setLastName("Administrator");
            defaultUser.setLogin("admin");
            defaultUser.setPassword("password");
            defaultUser.setToken("some-token");
            defaultUser.getRoles().add(Role.ADMIN);
            defaultUser.setEnabled(true);
            userDao.createOrUpdate(defaultUser);

        }

    }

    @Override
    public Optional<UserDto> getUserByLogin(String login) {
        return Optional.ofNullable(userDao.getUser(login));
    }

    @Override
    public Optional<UserDto> getAuthenticatedUser(String login, String password) {
        UserDto userDto = userDao.getUser(login);
        if (userDto == null) {
            return Optional.empty();
        }

        if (!userDto.isEnabled()) {
            return Optional.empty();
        }

        if (userDao.isValidPassword(userDto.getId(), password)) {
            return Optional.of(userDto);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserDto> getAuthenticatedUser(String token) {
        return Optional.ofNullable(userDao.getUserByToken(token));
    }

    @Override
    public void resetToken(Long userId) {
        UserDto userDto = userDao.getUser(userId);
        userDto.setToken(UUID.randomUUID().toString());
        userDao.createOrUpdate(userDto);
    }

}
