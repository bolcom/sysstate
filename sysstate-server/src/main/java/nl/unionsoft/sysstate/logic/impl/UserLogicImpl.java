package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.dao.UserDao;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("userLogic")
public class UserLogicImpl implements UserLogic, InitializingBean {

    @Inject
    @Named("userDao")
    private UserDao userDao;

    public UserDto getCurrentUser() {
        UserDto user = null;
        final String login = SecurityContextHolder.getContext().getAuthentication().getName();
        if (StringUtils.isNotBlank(login)) {
            user = userDao.getUser(login);
        }
        return user;
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
        final UserDto currentUser = getCurrentUser();
        if (currentUser != null) {
            if (userId.equals(currentUser.getId())) {
                throw new IllegalStateException("You can't delete yourself!");
            }
        }
        userDao.delete(userId);

    }

    public void afterPropertiesSet() throws Exception {
        List<UserDto> users = userDao.getUsers();
        if (users == null || users.size() == 0) {
            UserDto defaultUser = new UserDto();
            defaultUser.setFirstName("System");
            defaultUser.setLastName("Administrator");
            defaultUser.setLogin("admin");
            defaultUser.setPassword("password");
            defaultUser.getRoles().add("ROLE_ADMIN");
            defaultUser.setEnabled(true);
            userDao.createOrUpdate(defaultUser);

        }

    }

    public List<String> getRoles() {
        final List<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_EDITOR");
        return roles;
    }

}
