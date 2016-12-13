package nl.unionsoft.sysstate.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.EnvironmentList;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;
import nl.unionsoft.sysstate.sysstate_1_0.Text;

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
        assertNotNull(project.getId());
        assertEquals(project.getName(), randomProjectName);
        assertArrayEquals(project.getTags().toArray(), new String[] { "Hello" });
        assertProjectExists(randomProjectName);

        Project fetchedProject = sysState.getProject(project.getId());
        assertEquals(fetchedProject.getId(), project.getId());
        assertEquals(fetchedProject.getName(), project.getName());
        assertArrayEquals(fetchedProject.getTags().toArray(), new String[] { "Hello" });

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
        assertNotNull(instanceList);
        assertNotNull(instanceList.getInstances());

        Instance instance = createInstance(randomInstanceName, randomReference);
        Instance createdInstance = sysState.createInstance(instance);

        assertNotNull(createdInstance.getId());
        assertEquals(createdInstance.isEnabled(), true);
        assertEquals(createdInstance.getHomepageUrl(), "http://www.google.nl");
        assertEquals(createdInstance.getName(), randomInstanceName);
        assertEquals(createdInstance.getPlugin(), "mockStateResolver");
        assertEquals(createdInstance.getReference(), randomReference);
        assertEquals(createdInstance.getRefreshTimeout(), 10000);
        assertArrayEquals(createdInstance.getTags().toArray(), new String[] { "test" });

        Instance fetchedInstance = sysState.getInstance(createdInstance.getId());
        assertEquals(fetchedInstance.isEnabled(), true);
        assertEquals(fetchedInstance.getHomepageUrl(), "http://www.google.nl");
        assertEquals(fetchedInstance.getName(), randomInstanceName);
        assertEquals(fetchedInstance.getPlugin(), "mockStateResolver");
        assertEquals(fetchedInstance.getReference(), randomReference);
        assertEquals(fetchedInstance.getRefreshTimeout(), 10000);
        assertArrayEquals(fetchedInstance.getTags().toArray(), new String[] { "test" });

        sysState.deleteInstance(fetchedInstance.getId());

    }

    @Test
    public void testCreateUpdateDeleteEnvironments() {
        String randomEnvironmentName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        assertEnvironmentExists("MOCK");
        assertEnvironmentExists("PROD");

        Environment environment = createEnvironment(randomEnvironmentName, "Hello");
        assertNotNull(environment.getId());
        assertEquals(environment.getName(), randomEnvironmentName);
        assertArrayEquals(environment.getTags().toArray(), new String[] { "Hello" });
        assertEnvironmentExists(randomEnvironmentName);

        Environment fetchedEnvironment = sysState.getEnvironment(environment.getId());
        assertEquals(fetchedEnvironment.getId(), environment.getId());
        assertEquals(fetchedEnvironment.getName(), environment.getName());
        assertArrayEquals(fetchedEnvironment.getTags().toArray(), new String[] { "Hello" });

        String secondRandomEnvironmentName = StringUtils.upperCase(StringUtils.substring(UUID.randomUUID().toString(), 0, 6));
        fetchedEnvironment.setName(secondRandomEnvironmentName);
        sysState.updateEnvironment(fetchedEnvironment.getId(), fetchedEnvironment);
        assertEnvironmentDoesNotExists(randomEnvironmentName);
        assertEnvironmentExists(secondRandomEnvironmentName);

        sysState.deleteEnvironment(environment.getId());

        assertEnvironmentDoesNotExists(randomEnvironmentName);
        assertEnvironmentDoesNotExists(secondRandomEnvironmentName);
    }

    @Test
    public void testCreateCreateUpdateDeleteTexts() {

        Text text = createText("integrationTest", "Hello World!", new String[] { "Test", "Blaat" });
        sysState.createOrUpdateText(text);

        Text result = sysState.getText("integrationTest");
        assertNotNull(result);
        assertEquals(result.getName(), text.getName());
        assertEquals(result.getText(), text.getText());
        assertEquals(result.getTags(), text.getTags());

        Text text2 = createText("integrationTest", "Bye World!", new String[] { "Test", "Blaat" });
        sysState.createOrUpdateText(text2);

        Text result2 = sysState.getText("integrationTest");
        assertNotNull(result2);
        assertEquals(result2.getName(), text2.getName());
        assertEquals(result2.getText(), text2.getText());
        assertEquals(result2.getTags(), text2.getTags());

        sysState.deleteText("integrationTest");
        assertFalse(sysState.getTexts().getTexts().stream().filter(t -> t.getName().equals("integrationTest")).findFirst().isPresent());

    }

    private Text createText(String name, String contents, String[] tags) {
        Text text = new Text();
        text.setName(name);
        text.setText(contents);
        text.getTags().addAll(Arrays.asList(tags));
        return text;
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
        assertNotNull(projectList);
        List<Project> projects = projectList.getProjects();
        assertNotNull(projects);
        assertProjectDoesNotExists(projects, name);
    }

    private void assertProjectDoesNotExists(List<Project> projects, String name) {
        Optional<Project> optProject = projects.stream().filter(p -> p.getName().equals(name)).findFirst();
        assertFalse(optProject.isPresent());

    }

    public void assertProjectExists(String name) {
        ProjectList projectList = sysState.getProjects();
        assertNotNull(projectList);
        List<Project> projects = projectList.getProjects();
        assertNotNull(projects);
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
        assertTrue(optProject.isPresent());
    }

    public void assertEnvironmentDoesNotExists(String name) {
        EnvironmentList environmentList = sysState.getEnvironments();
        assertNotNull(environmentList);
        List<Environment> environments = environmentList.getEnvironments();
        assertNotNull(environments);
        assertEnvironmentDoesNotExists(environments, name);
    }

    private void assertEnvironmentDoesNotExists(List<Environment> environments, String name) {
        Optional<Environment> optEnvironment = environments.stream().filter(p -> p.getName().equals(name)).findFirst();
        assertFalse(optEnvironment.isPresent());

    }

    public void assertEnvironmentExists(String name) {
        EnvironmentList environmentList = sysState.getEnvironments();
        assertNotNull(environmentList);
        List<Environment> environments = environmentList.getEnvironments();
        assertNotNull(environments);
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
        assertTrue(optEnvironment.isPresent());
    }

}
