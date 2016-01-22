package nl.unionsoft.sysstate.logic;

import java.util.List;
import java.util.Optional;

import nl.unionsoft.sysstate.dto.UserDto;

public interface UserLogic {

    public Optional<UserDto> getCurrentUser();

    public List<UserDto> getUsers();

    public void createOrUpdate(UserDto user);

    public UserDto getUser(Long userId);
    
    public UserDto getUserByLogin(String userName);

    public void delete(Long userId);
    
    public List<String> getRoles();

}
