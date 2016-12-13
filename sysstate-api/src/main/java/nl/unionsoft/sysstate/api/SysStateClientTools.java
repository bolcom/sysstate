package nl.unionsoft.sysstate.api;

import org.apache.commons.lang.StringUtils;

import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

public class SysStateClientTools {

    public static ProjectEnvironment createProjectEnvironment(String projectName, String environmentName) {
        ProjectEnvironment projectEnvironment = new ProjectEnvironment();
        Project project = new Project();
        project.setName(StringUtils.upperCase(projectName));
        projectEnvironment.setProject(project);
        Environment environment = new Environment();
        environment.setName(StringUtils.upperCase(environmentName));
        projectEnvironment.setEnvironment(environment);
        return projectEnvironment;
    }

}
