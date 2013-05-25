package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.io.IOException;

import mockit.Mocked;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

public class JenkinsJobStateResolverTest {

    private static final String STABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-job-state-resolver-plugin-data-stable.xml";
    private static final String UNSTABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-job-state-resolver-plugin-data-unstable.xml";
    private static final String ERROR = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-job-state-resolver-plugin-data-error.xml";
    private static final String PENDING = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-job-state-resolver-plugin-data-pending.xml";
    private static final String DISABLED = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-job-state-resolver-plugin-data-disabled.xml";

    private JenkinsJobStateResolverImpl plugin;
    
    @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Before
    public void before() {
        plugin = new JenkinsJobStateResolverImpl();
        plugin.setXmlBeansMarshaller(new XmlBeansMarshaller());
        plugin.setHttpClient(defaultHttpClient);
    }

    @Test
    public void testStable() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, STABLE);
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("STABLE", state.getDescription());
        Assert.assertEquals("", state.getMessage());
    }

    @Test
    public void testUnstable() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, UNSTABLE);
        Assert.assertEquals(StateType.UNSTABLE, state.getState());
        Assert.assertEquals("UNSTABLE", state.getDescription());
        Assert.assertEquals("", state.getMessage());
    }

    @Test
    public void testError() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, ERROR);
        Assert.assertEquals(StateType.ERROR, state.getState());
        Assert.assertEquals("FAILED", state.getDescription());
        Assert.assertEquals("", state.getMessage());
    }

    @Test
    public void testPending() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, PENDING);
        Assert.assertEquals(StateType.PENDING, state.getState());
        Assert.assertEquals("PENDING", state.getDescription());
        Assert.assertEquals("Project is awaiting (first) build...", state.getMessage());
    }

    @Test
    public void testDisabled() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, DISABLED);
        Assert.assertEquals(StateType.DISABLED, state.getState());
        Assert.assertEquals("DISABLED", state.getDescription());
        Assert.assertEquals("Project is disabled, visit project homepage for more information!", state.getMessage());
    }

}
