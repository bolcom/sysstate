package nl.unionsoft.sysstate.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;

public interface SysState {

    @GET
    @Path("/api/instance")
    InstanceList getInstances();

    @POST
    @Path("/api/instance")
    Instance createInstance(Instance instance);

    @PUT
    @Path("/api/instance/{instanceId}")
    void updateInstance(@QueryParam("instanceId") Long instanceId, Instance instance);

    @DELETE
    @Path("/api/instance/{instanceId}")
    void deleteInstance(@QueryParam("instanceId") Long instanceId);

    @GET
    @Path("/api/project")
    ProjectList getProjects();

    @POST
    @Path("/api/project")
    Project createProject(Project project);

    @PUT
    @Path("/api/project/{projectId}")
    void updateProject(@QueryParam("projectId") Long projectId, Project project);

    @DELETE
    @Path("/api/project/{projectId}")
    void deleteProject(@QueryParam("projectId") Long projectId);
    
}
