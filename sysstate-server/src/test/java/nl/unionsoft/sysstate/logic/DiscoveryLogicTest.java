package nl.unionsoft.sysstate.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mockit.Expectations;
import mockit.Mocked;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.extending.Discovery;
import nl.unionsoft.sysstate.common.logic.DiscoveryLogic;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.domain.Environment;
import nl.unionsoft.sysstate.logic.impl.DiscoveryLogicImpl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class DiscoveryLogicTest {

    private DiscoveryLogic discoveryLogic;
    @Mocked
    private Discovery discoveryPlugin;

    @Mocked
    private InstanceLogic instanceLogic;

    @Mocked
    private ProjectLogic projectLogic;

    @Mocked
    private EnvironmentLogic environmentLogic;

    @Before
    public void before() {
        discoveryLogic = new DiscoveryLogicImpl();
        //        ReflectionTestUtils.setField(discoveryLogic, "pluginLogic", pluginLogic);
        ReflectionTestUtils.setField(discoveryLogic, "instanceLogic", instanceLogic);
        ReflectionTestUtils.setField(discoveryLogic, "environmentLogic", environmentLogic);
        ReflectionTestUtils.setField(discoveryLogic, "projectLogic", projectLogic);

    }

    @Test
    public void testDiscover() {
        final Properties properties = new Properties();
        final List<InstanceDto> currentInstances = new ArrayList<InstanceDto>();
        final List<InstanceDto> discoveredInstances = new ArrayList<InstanceDto>();
        {
            final InstanceDto instance = new InstanceDto();
            instance.setProjectEnvironment(createProjectEnv());
            instance.setConfiguration("harry=potter");
            instance.setPluginClass("thePluginClass");
            discoveredInstances.add(instance);
        }

        new Expectations() {
            {
                // @formatter:off
                // pluginLogic.getPlugin("discoveryPlugin");
                result = discoveryPlugin;
                discoveryPlugin.discover(properties);
                result = discoveredInstances;
                instanceLogic.getInstances();
                result = currentInstances;
                final ProjectDto project = new ProjectDto();
                project.setId(1L);
                projectLogic.findProject("PRJ");
                result = project;
                final Environment environment = new Environment();
                environment.setId(2L);
                environmentLogic.findEnvironment("ENV");
                result = environment;
                // @formatter:on
            }
        };
        // final List<? extends InstanceDto> instances = (List<? extends InstanceDto>) discoveryLogic.discover("discoveryPlugin", properties);
        // Assert.assertNotNull(instances);
        // Assert.assertEquals(1, instances.size());
        // {
        // final InstanceDto instance = instances.get(0);
        // Assert.assertEquals(Long.valueOf(1L), instance.getProjectEnvironment().getProject().getId());
        // Assert.assertEquals(Long.valueOf(2L), instance.getProjectEnvironment().getEnvironment().getId());
        // }
    }

    @Test
    public void testDiscoverDuplicate() {
        final Properties properties = new Properties();
        final List<InstanceDto> currentInstances = new ArrayList<InstanceDto>();
        {
            final InstanceDto instance = new InstanceDto();
            instance.setProjectEnvironment(createProjectEnv());
            instance.setConfiguration("harry=potter");
            instance.setPluginClass("thePluginClass");
            currentInstances.add(instance);
        }

        final List<InstanceDto> discoveredInstances = new ArrayList<InstanceDto>();
        {
            final InstanceDto instance = new InstanceDto();
            instance.setProjectEnvironment(createProjectEnv());
            instance.setConfiguration("harry=potter");
            instance.setPluginClass("thePluginClass");
            discoveredInstances.add(instance);
        }

        new Expectations() {
            {
                // @formatter:off
                //                pluginLogic.getPlugin("discoveryPlugin");
                result = discoveryPlugin;
                discoveryPlugin.discover(properties);
                result = discoveredInstances;
                instanceLogic.getInstances();
                result = currentInstances;
                // @formatter:on
            }
        };
        // final List<? extends InstanceDto> instances = (List<? extends InstanceDto>)
        // discoveryLogic.discover("discoveryPlugin", properties);
        // Assert.assertNotNull(instances);
        // Assert.assertEquals(0, instances.size());

    }

    private ProjectEnvironmentDto createProjectEnv() {
        final ProjectEnvironmentDto projectEnvironment = new ProjectEnvironmentDto();
        final ProjectDto project = new ProjectDto();
        project.setName("PRJ");
        projectEnvironment.setProject(project);
        final EnvironmentDto environment = new EnvironmentDto();
        environment.setName("ENV");
        projectEnvironment.setEnvironment(environment);
        return projectEnvironment;
    }
}
