package nl.unionsoft.sysstate.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

public class UserDto {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private boolean enabled;
    private String configuration;
    private List<Role> roles;

    public UserDto() {
        roles = new ArrayList<Role>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserDto [id=" + id + ", login=" + login + ", firstName=" + firstName + ", lastName=" + lastName + ", enabled=" + enabled + ", roles=" + roles
                + "]";
    }

    public enum Role {
        ADMIN, EDITOR, ANONYMOUS;

        public static boolean isExistingRole(String roleName) {
            for (Role role : Role.values()) {
                if (StringUtils.equals(role.name(), roleName)) {
                    return true;
                }
            }
            return false;
        }

        public static List<Role> getAssignableRoles() {
            //@formatter:off            
            return Arrays.stream(values())
                    .filter(r -> !r.equals(Role.ANONYMOUS))
                    .collect(Collectors.toList());
            //@formatter:on
        }
    }

}
