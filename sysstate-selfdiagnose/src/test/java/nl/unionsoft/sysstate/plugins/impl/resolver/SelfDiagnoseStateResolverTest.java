package nl.unionsoft.sysstate.plugins.impl.resolver;

import java.io.IOException;

import mockit.Mocked;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.plugins.selfdiagnose.SelfDiagnoseStateResolverConfig;
import nl.unionsoft.sysstate.plugins.selfdiagnose.SelfDiagnoseStateResolverImpl;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

public class SelfDiagnoseStateResolverTest {
    private static final String STABLE_MULTI = "/nl/unionsoft/sysstate/plugins/impl/resolver/self-diagnose-state-resolver-plugin-stable-multi.xml";
    private static final String STABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/self-diagnose-state-resolver-plugin-stable.xml";
    private static final String UNSTABLE = "/nl/unionsoft/sysstate/plugins/impl/resolver/self-diagnose-state-resolver-plugin-unstable.xml";
    private static final String ERROR = "/nl/unionsoft/sysstate/plugins/impl/resolver/self-diagnose-state-resolver-plugin-error.xml";
    private static final String NO_VERSION = "/nl/unionsoft/sysstate/plugins/impl/resolver/self-diagnose-state-resolver-plugin-no-version.xml";

    private SelfDiagnoseStateResolverImpl plugin;
    @Mocked
    private DefaultHttpClient defaultHttpClient;

    @Before
    public void before() {
        plugin = new SelfDiagnoseStateResolverImpl();
        plugin.setXmlBeansMarshaller(new XmlBeansMarshaller());
        plugin.setHttpClient(defaultHttpClient);
    }

    @Test
    public void testStable() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, STABLE, new SelfDiagnoseStateResolverConfig());
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("1.2.3.4", state.getDescription());
        Assert.assertEquals("", state.getMessage());

    }

    @Test
    public void testStableMulti2() throws IOException {
        SelfDiagnoseStateResolverConfig  selfDiagnoseStateResolverConfig = new SelfDiagnoseStateResolverConfig();
        selfDiagnoseStateResolverConfig.setPattern( ".*mehehehe.*");

        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, STABLE_MULTI, selfDiagnoseStateResolverConfig);
        System.out.println(state.getMessage());
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("9.0.1.2", state.getDescription());
        Assert.assertEquals("", state.getMessage());

    }

    @Test
    public void testStableMulti() throws IOException {

        SelfDiagnoseStateResolverConfig selfDiagnoseStateResolverConfig = new SelfDiagnoseStateResolverConfig();
        selfDiagnoseStateResolverConfig.setPattern(".*Maven POM properties.*");

        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, STABLE_MULTI, selfDiagnoseStateResolverConfig);
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("5.6.7.8", state.getDescription());
        Assert.assertEquals("", state.getMessage());

    }

    @Test
    public void testUnStable() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, UNSTABLE, new SelfDiagnoseStateResolverConfig());
        Assert.assertEquals(StateType.UNSTABLE, state.getState());
        Assert.assertEquals("1.2.3.4", state.getDescription());
        Assert.assertEquals("checkspringdatasourceconnectable: failed, Datasource [AQDataSource] is found and a connection can be created.", state.getMessage());

    }

    @Test
    public void testError() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, ERROR, new SelfDiagnoseStateResolverConfig());
        Assert.assertEquals(StateType.ERROR, state.getState());
        Assert.assertEquals("1.2.3.4", state.getDescription());
        Assert.assertEquals("reportmavenpomproperties: failed, Version=1.2.3.4 build=Fri Nov 16 03:35:25 CET 2012 from [/META-INF/maven/com.bol.myapp/myapp/pom.properties]\n"
                + "checkspringdatasourceconnectable: failed, Datasource [AQDataSource] is found but a connection cannot be created.", state.getMessage());

    }

    @Test
    public void testNoVersion() throws IOException {
        final StateDto state = HttpTestUtil.doCall(plugin, defaultHttpClient, NO_VERSION, new SelfDiagnoseStateResolverConfig());
        Assert.assertEquals(StateType.STABLE, state.getState());
        Assert.assertEquals("Status 200", state.getDescription());
        Assert.assertEquals("", state.getMessage());

    }
}
