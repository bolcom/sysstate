package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.dto.UserDto;

public interface UserDao {

    public UserDto getUser(String login);

    public List<UserDto> getUsers();

    public void createOrUpdate(UserDto user);

    public UserDto getUser(Long userId);

    public void delete(Long userId);
}
