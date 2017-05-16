package nl.unionsoft.sysstate.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.dao.UserDao;
import nl.unionsoft.sysstate.domain.User;
import nl.unionsoft.sysstate.domain.UserRole;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.dto.UserDto.Role;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userDao")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserDaoImpl implements UserDao {

    @Inject
    @Named("userConverter")
    private Converter<UserDto, User> userConverter;

    @Inject
    @Named("entityManager")
    private EntityManager entityManager;

    public UserDto getUser(final String login) {
        User user = null;
        try {
            // @formatter:off
            user = entityManager.createQuery(
                    "FROM User user " +
                            "WHERE user.login = :login", User.class)
                            .setHint("org.hibernate.cacheable", true)
                            .setMaxResults(1)
                            .setParameter("login", login).getSingleResult();
            // @formatter:on
        } catch (final NoResultException nre) {
            // Nothing to see here!
        }
        return userConverter.convert(user);
    }

    public List<UserDto> getUsers() {
        return ListConverter.convert(userConverter, entityManager.createQuery("FROM User", User.class).getResultList());
    }

    public void createOrUpdate(final UserDto dto) {

        final boolean persist = dto.getId() == null;
        User user = null;
        if (persist) {
            user = new User();
        } else {
            user = entityManager.find(User.class, dto.getId());
        }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setLogin(dto.getLogin());
        if (StringUtils.isNotEmpty(dto.getToken())) {
            user.setToken(dto.getToken());
        }
        if (StringUtils.isNotEmpty(dto.getPassword())) {
            user.setPassword(hash(dto.getPassword()));
        }
        user.setEnabled(dto.isEnabled());
        user.setConfiguration(dto.getConfiguration());
        if (persist) {
            entityManager.persist(user);
        }

        entityManager.createQuery("DELETE FROM UserRole WHERE user = :user").setParameter("user", user).executeUpdate();
        final List<Role> roles = dto.getRoles();
        if (roles != null) {
            for (final Role role : roles) {
                final UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setAuthority(role.name());
                entityManager.persist(userRole);
            }

        }

    }

    private String hash(final String value) {
        return DigestUtils.md5Hex(value);

    }

    public boolean isValidPassword(final Long userId, String password) {
        User user = entityManager.find(User.class, userId);
        return StringUtils.equals(user.getPassword(), hash(password));

    }

    public UserDto getUser(final Long userId) {
        return userConverter.convert(entityManager.find(User.class, userId));
    }

    public void delete(final Long userId) {
        entityManager.remove(entityManager.find(User.class, userId));

    }

    @Override
    public UserDto getUserByToken(String token) {
        return userConverter.convert(entityManager.createQuery("FROM User WHERE token = :token", User.class).setParameter("token", token).getSingleResult());
    }

}
