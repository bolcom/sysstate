package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.dto.UserDto;

public interface UserLogic {

    public UserDto getCurrentUser();

    public List<UserDto> getUsers();

    public void createOrUpdate(UserDto user);

    public UserDto getUser(Long userId);

    public void delete(Long userId);
    
    public List<String> getRoles();

}
