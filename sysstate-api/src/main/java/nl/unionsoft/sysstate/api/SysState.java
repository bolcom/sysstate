package nl.unionsoft.sysstate.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;

public interface SysState {

    @GET
    @Path("/api/instance")
    InstanceList getInstances();

    @GET
    @Path("/api/instance/{instanceId}")
    Instance getInstance(@QueryParam("instanceId") Long instanceId);
    
    @POST
    @Path("/api/instance")
    Instance createInstance(Instance instance);

    /**
     * Update the given instance based on id or reference. If the id specified, the reference will be updated. If only the reference is specified, the instance
     * with the same reference will be looked up.
     * 
     * @param instance
     *            the instance to be updated.
     */
    @PUT
    @Path("/api/instance/")
    void updateInstance(Instance instance);

    @DELETE
    @Path("/api/instance/{instanceId}")
    void deleteInstance(@QueryParam("instanceId") Long instanceId);

    @GET
    @Path("/api/project")
    ProjectList getProjects();

    @GET
    @Path("/api/project/{projectId}")
    Project getProject(@QueryParam("projectId") Long projectId);

    
    @POST
    @Path("/api/project")
    Project createProject(Project project);

    @PUT
    @Path("/api/project/{projectId}")
    void updateProject(@QueryParam("projectId") Long projectId, Project project);

    @DELETE
    @Path("/api/project/{projectId}")
    void deleteProject(@QueryParam("projectId") Long projectId);

    @GET
    @Path("/api/projectenvironment")
    //@formatter:off
    ProjectEnvironment getProjectEnvironment(
            @QueryParam("projectName") String projectName, 
            @QueryParam("environmentName") String environmentName);
    //@formatter:on

    @GET
    @Path("/api/projectenvironment")
    //@formatter:off
    ProjectEnvironment getProjectEnvironment(
            @QueryParam("projectName") String projectName, 
            @QueryParam("environmentName") String environmentName,
            @QueryParam("state") StateBehaviour state);
    //@formatter:on

}
