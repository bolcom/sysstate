package nl.unionsoft.sysstate.plugins.impl.discovery;

import java.util.Collection;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

public class JenkinsNodesDiscoveryPluginTest {

    private JenkinsNodesDiscoveryPluginImpl plugin;

    @Before
    public void before() {
        plugin = new JenkinsNodesDiscoveryPluginImpl();
        plugin.setXmlBeansMarshaller(new XmlBeansMarshaller());
        plugin.setHttpClient(new DefaultHttpClient());
    }

    @Test
    public void test() {
        final Properties properties = new Properties();
        properties.setProperty("url", "http://jenkins/computer/");
        final Collection<? extends ReferenceRunnable> instances = plugin.discover(properties);
        for (final ReferenceRunnable instance : instances) {
            // System.out.println(instance.getHomepageUrl());
        }

    }
}
