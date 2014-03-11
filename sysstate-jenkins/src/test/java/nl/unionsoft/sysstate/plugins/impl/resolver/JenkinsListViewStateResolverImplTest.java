package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.io.IOException;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.HttpClientLogic;
import nl.unionsoft.sysstate.plugins.jenkins.JenkinsListViewStateResolverImpl;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

public class JenkinsListViewStateResolverImplTest {

    private static final String MIXED = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-view-state-resolver-plugin-data-mixed.xml";
    private static final String STABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-view-state-resolver-plugin-data-stable.xml";
    private static final String UNSTABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/jenkins-view-state-resolver-plugin-data-unstable.xml";

    private JenkinsListViewStateResolverImpl plugin;

    @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Mocked
    private HttpClientLogic httpClientLogic;

    @Before
    public void before() {
        plugin = new JenkinsListViewStateResolverImpl();
        plugin.setXmlBeansMarshaller(new XmlBeansMarshaller());
        plugin.setHttpClientLogic(httpClientLogic);
        //@formatter:off
        new NonStrictExpectations() {{
                httpClientLogic.getHttpClient("default");result = defaultHttpClient;
        }};
        //@formatter:on
    }

    @Test
    public void testMixed() throws IOException {
        new NonStrictExpectations() {
            {
                httpClientLogic.getHttpClient("default");
                result = defaultHttpClient;
            }
        };
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, MIXED);
        Assert.assertEquals(StateType.ERROR, state.getState());
    }

    @Test
    public void testStable() throws IOException {
        new NonStrictExpectations() {
            {
                httpClientLogic.getHttpClient("default");
                result = defaultHttpClient;
            }
        };
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, STABLE);
        Assert.assertEquals(StateType.STABLE, state.getState());
    }

    @Test
    public void testUnstable() throws IOException {
        new NonStrictExpectations() {
            {
                httpClientLogic.getHttpClient("default");
                result = defaultHttpClient;
            }
        };
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, UNSTABLE);
        Assert.assertEquals(StateType.UNSTABLE, state.getState());
    }

    
}
