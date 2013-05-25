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

public class JenkinsServerStateResolverTest {

    private static final String ONLINE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-server-state-resolver-plugin-data-online.xml";
    private static final String OFFLINE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-server-state-resolver-plugin-data-offline.xml";
    private static final String TEMP_OFFLINE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-server-state-resolver-plugin-data-temp-offline.xml";

    private JenkinsServerStateResolverImpl plugin;
    @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Before
    public void before() {
        plugin = new JenkinsServerStateResolverImpl();
        plugin.setXmlBeansMarshaller(new XmlBeansMarshaller());
        plugin.setHttpClient(defaultHttpClient);
    }

    @Test
    public void testOnline() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, ONLINE);
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("ONLINE", state.getDescription());
        Assert.assertEquals("Instance has '4' executors.", state.getMessage());

    }

    @Test
    public void testOffline() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, OFFLINE);
        Assert.assertEquals(StateType.ERROR, state.getState());
        Assert.assertEquals("OFFLINE", state.getDescription());
        Assert.assertEquals("Instance has '4' executors.\nInstance is offline!", state.getMessage());

    }

    @Test
    public void testTempOffline() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, TEMP_OFFLINE);
        Assert.assertEquals(StateType.ERROR, state.getState());
        Assert.assertEquals("OFFLINE", state.getDescription());
        Assert.assertEquals("Instance has '4' executors.\nInstance is temporarily offline!", state.getMessage());

    }

}
