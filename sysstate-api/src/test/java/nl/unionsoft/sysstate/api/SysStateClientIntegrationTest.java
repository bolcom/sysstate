package nl.unionsoft.sysstate.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.EnvironmentList;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;

@Ignore
public class SysStateClientIntegrationTest {

    private SysState sysState;

    @Before
    public void before() {
        sysState = SysStateClient.getSysState("http://localhost:8680", "some-token");
    }

    @Test
    public void testCreateUpdateDeleteProjects() {
        String randomProjectName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        assertProjectExists("ILSE");
        assertProjectExists("YAHO");
        assertProjectExists("BING");
        assertProjectExists("GOOG");

        Project project = createProject(randomProjectName, "Hello");
        Assert.assertNotNull(project.getId());
        Assert.assertEquals(project.getName(), randomProjectName);
        Assert.assertArrayEquals(project.getTags().toArray(), new String[] { "Hello" });
        assertProjectExists(randomProjectName);

        Project fetchedProject = sysState.getProject(project.getId());
        Assert.assertEquals(fetchedProject.getId(), project.getId());
        Assert.assertEquals(fetchedProject.getName(), project.getName());
        Assert.assertArrayEquals(fetchedProject.getTags().toArray(), new String[] { "Hello" });

        String secondRandomProjectName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        fetchedProject.setName(secondRandomProjectName);
        sysState.updateProject(fetchedProject.getId(), fetchedProject);
        assertProjectDoesNotExists(randomProjectName);
        assertProjectExists(secondRandomProjectName);

        sysState.deleteProject(project.getId());

        assertProjectDoesNotExists(randomProjectName);
        assertProjectDoesNotExists(secondRandomProjectName);
    }

    @Test
    public void testCreateUpdateDeleteInstances() {
        String randomInstanceName = StringUtils.substring(UUID.randomUUID().toString(), 0, 12);
        String randomReference = UUID.randomUUID().toString();
        InstanceList instanceList = sysState.getInstances();
        Assert.assertNotNull(instanceList);
        Assert.assertNotNull(instanceList.getInstances());

        Instance instance = createInstance(randomInstanceName, randomReference);
        Instance createdInstance = sysState.createInstance(instance);

        Assert.assertNotNull(createdInstance.getId());
        Assert.assertEquals(createdInstance.isEnabled(), true);
        Assert.assertEquals(createdInstance.getHomepageUrl(), "http://www.google.nl");
        Assert.assertEquals(createdInstance.getName(), randomInstanceName);
        Assert.assertEquals(createdInstance.getPlugin(), "mockStateResolver");
        Assert.assertEquals(createdInstance.getReference(), randomReference);
        Assert.assertEquals(createdInstance.getRefreshTimeout(), 10000);
        Assert.assertArrayEquals(createdInstance.getTags().toArray(), new String[] { "test" });

        Instance fetchedInstance = sysState.getInstance(createdInstance.getId());
        Assert.assertEquals(fetchedInstance.isEnabled(), true);
        Assert.assertEquals(fetchedInstance.getHomepageUrl(), "http://www.google.nl");
        Assert.assertEquals(fetchedInstance.getName(), randomInstanceName);
        Assert.assertEquals(fetchedInstance.getPlugin(), "mockStateResolver");
        Assert.assertEquals(fetchedInstance.getReference(), randomReference);
        Assert.assertEquals(fetchedInstance.getRefreshTimeout(), 10000);
        Assert.assertArrayEquals(fetchedInstance.getTags().toArray(), new String[] { "test" });

        sysState.deleteInstance(fetchedInstance.getId());

    }

    @Test
    public void testCreateUpdateDeleteEnvironments() {
        String randomEnvironmentName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        assertEnvironmentExists("MOCK");
        assertEnvironmentExists("PROD");

        Environment environment = createEnvironment(randomEnvironmentName, "Hello");
        Assert.assertNotNull(environment.getId());
        Assert.assertEquals(environment.getName(), randomEnvironmentName);
        Assert.assertArrayEquals(environment.getTags().toArray(), new String[] { "Hello" });
        assertEnvironmentExists(randomEnvironmentName);

        Environment fetchedEnvironment = sysState.getEnvironment(environment.getId());
        Assert.assertEquals(fetchedEnvironment.getId(), environment.getId());
        Assert.assertEquals(fetchedEnvironment.getName(), environment.getName());
        Assert.assertArrayEquals(fetchedEnvironment.getTags().toArray(), new String[] { "Hello" });

        String secondRandomEnvironmentName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        fetchedEnvironment.setName(secondRandomEnvironmentName);
        sysState.updateEnvironment(fetchedEnvironment.getId(), fetchedEnvironment);
        assertEnvironmentDoesNotExists(randomEnvironmentName);
        assertEnvironmentExists(secondRandomEnvironmentName);

        sysState.deleteEnvironment(environment.getId());

        assertEnvironmentDoesNotExists(randomEnvironmentName);
        assertEnvironmentDoesNotExists(secondRandomEnvironmentName);
    }

    public Instance createInstance(String instanceName, String reference) {
        Instance instance = new Instance();
        instance.setEnabled(true);
        instance.setHomepageUrl("http://www.google.nl");
        instance.setName(instanceName);
        instance.setPlugin("mockStateResolver");
        instance.setProjectEnvironment(SysStateClientTools.createProjectEnvironment("GOOG", "MOCK"));
        instance.setReference(reference);
        instance.getTags().add("test");
        instance.setRefreshTimeout(10000);
        return instance;
    }

    public void assertProjectDoesNotExists(String name) {
        ProjectList projectList = sysState.getProjects();
        Assert.assertNotNull(projectList);
        List<Project> projects = projectList.getProjects();
        Assert.assertNotNull(projects);
        assertProjectDoesNotExists(projects, name);
    }

    private void assertProjectDoesNotExists(List<Project> projects, String name) {
        Optional<Project> optProject = projects.stream().filter(p -> p.getName().equals(name)).findFirst();
        Assert.assertFalse(optProject.isPresent());

    }

    public void assertProjectExists(String name) {
        ProjectList projectList = sysState.getProjects();
        Assert.assertNotNull(projectList);
        List<Project> projects = projectList.getProjects();
        Assert.assertNotNull(projects);
        assertProjectExists(projects, name);
    }

    public Project createProject(String name, String... tags) {
        Project project = new Project();
        project.setName(name);
        if (tags != null) {
            project.getTags().addAll(Arrays.asList(tags));
        }

        return sysState.createProject(project);
    }

    private void assertProjectExists(List<Project> projects, String name) {
        Optional<Project> optProject = projects.stream().filter(p -> p.getName().equals(name)).findFirst();
        Assert.assertTrue(optProject.isPresent());
    }

    public void assertEnvironmentDoesNotExists(String name) {
        EnvironmentList environmentList = sysState.getEnvironments();
        Assert.assertNotNull(environmentList);
        List<Environment> environments = environmentList.getEnvironments();
        Assert.assertNotNull(environments);
        assertEnvironmentDoesNotExists(environments, name);
    }

    private void assertEnvironmentDoesNotExists(List<Environment> environments, String name) {
        Optional<Environment> optEnvironment = environments.stream().filter(p -> p.getName().equals(name)).findFirst();
        Assert.assertFalse(optEnvironment.isPresent());

    }

    public void assertEnvironmentExists(String name) {
        EnvironmentList environmentList = sysState.getEnvironments();
        Assert.assertNotNull(environmentList);
        List<Environment> environments = environmentList.getEnvironments();
        Assert.assertNotNull(environments);
        assertEnvironmentExists(environments, name);
    }

    public Environment createEnvironment(String name, String... tags) {
        Environment environment = new Environment();
        environment.setName(name);
        if (tags != null) {
            environment.getTags().addAll(Arrays.asList(tags));
        }

        return sysState.createEnvironment(environment);
    }

    private void assertEnvironmentExists(List<Environment> environments, String name) {
        Optional<Environment> optEnvironment = environments.stream().filter(p -> p.getName().equals(name)).findFirst();
        Assert.assertTrue(optEnvironment.isPresent());
    }

}
