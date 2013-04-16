package nl.unionsoft.sysstate.dao;

import java.util.List;

import nl.unionsoft.sysstate.domain.Environment;

public interface EnvironmentDao {

    public List<Environment> getEnvironments();

    public Environment getEnvironment(Long environmentId);

    public void createOrUpdate(Environment environment);

    public void delete(Long environmentId);

    public Environment findEnvironment(String name);

}
