package nl.unionsoft.sysstate.common.stateresolver.impl;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.plugins.http.JSoupStateResolverConfig;
import nl.unionsoft.sysstate.plugins.http.JSoupStateResolverImpl;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;

public class JSoupStateResolverImplTest {

    private JSoupStateResolverImpl jSoupStateResolverPlugin;

    @Before
    public void before() {
        final HttpClient httpClient = new DefaultHttpClient();
        jSoupStateResolverPlugin = new JSoupStateResolverImpl();
        jSoupStateResolverPlugin.setHttpClient(httpClient);
    }

    @Test
    public void doTest() {
        final StateDto state = new StateDto();
        final InstanceDto instance = new InstanceDto();

        JSoupStateResolverConfig jSoupStateResolverConfig = new JSoupStateResolverConfig();
        jSoupStateResolverConfig.setUrl("http://bolnl-docs/reporting/dashboard/db_rel_version.html");
        jSoupStateResolverConfig.setSelect("table td:contains(APM) ~ td:eq(3)");
        instance.setInstanceConfiguration(jSoupStateResolverConfig);

        jSoupStateResolverPlugin.setState(instance, state, null);

    }
}
