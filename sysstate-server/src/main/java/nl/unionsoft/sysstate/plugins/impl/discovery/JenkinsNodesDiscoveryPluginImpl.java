package nl.unionsoft.sysstate.plugins.impl.discovery;

import static nl.unionsoft.sysstate.util.XmlUtil.getCharacterDataFromObjectWithKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.common.util.PropertiesUtil;
import nl.unionsoft.sysstate.common.discovery.impl.XmlBeansDiscoveryPluginImpl;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.plugins.impl.resolver.JenkinsServerStateResolverPluginImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@PluginImplementation
public class JenkinsNodesDiscoveryPluginImpl extends XmlBeansDiscoveryPluginImpl {

    private static final String API_XML = "/api/xml";
    private final Properties pluginConfig;

    public JenkinsNodesDiscoveryPluginImpl () {
        pluginConfig = new Properties();
        pluginConfig.setProperty("env", "DEV");
        pluginConfig.setProperty("prj", "JEN");
    }

    @Override
    public Collection<? extends InstanceDto> handleXmlObject(XmlObject xmlObject, Properties configuration) {
        final List<InstanceDto> instances = new ArrayList<InstanceDto>();
        final Node node = xmlObject.getDomNode();
        final Document document = (Document) node;
        final NodeList computers = document.getElementsByTagName("computer");

        final String url = configuration.getProperty(URL);
        for (int i = 0; i < computers.getLength(); i++) {
            final Node childNode = computers.item(i);

            final InstanceDto instance = new InstanceDto();

            final ProjectEnvironmentDto projectEnvironment = new ProjectEnvironmentDto();

            final ProjectDto project = new ProjectDto();
            project.setName(PropertiesUtil.getProperty(configuration, pluginConfig, "prj"));
            projectEnvironment.setProject(project);

            final EnvironmentDto environment = new EnvironmentDto();
            environment.setName(PropertiesUtil.getProperty(configuration, pluginConfig, "env"));
            projectEnvironment.setEnvironment(environment);

            instance.setProjectEnvironment(projectEnvironment);

            final String displayName = getCharacterDataFromObjectWithKey(childNode, "displayName");
            instance.setName(displayName);
            instance.setEnabled(true);
            final Properties instanceConfig = new Properties();
            final StringBuilder urlBuilder = new StringBuilder(4012);
            urlBuilder.append(url);
            if (!StringUtils.endsWith(url, "/")) {
                urlBuilder.append('/');
            }

            if (StringUtils.equalsIgnoreCase("master", displayName)) {
                urlBuilder.append("(master)");
                instance.setTags("jenkins jenkins-master");
            } else {
                urlBuilder.append(displayName);
                instance.setTags("jenkins jenkins-slave");
            }
            urlBuilder.append('/');

            instanceConfig.setProperty(URL, urlBuilder.toString());
            instance.setConfiguration(PropertiesUtil.propertiesToString(instanceConfig));
            instance.setHomepageUrl(urlBuilder.toString());
            instance.setRefreshTimeout(60000);
            instance.setPluginClass(JenkinsServerStateResolverPluginImpl.class.getName());
            instances.add(instance);
        }
        return instances;

    }

    @Override
    public String processUri(String uri) {
        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(super.processUri(uri));
        if (!StringUtils.endsWith(uri, API_XML)) {
            uriBuilder.append(API_XML);
        }
        return uriBuilder.toString();
    }

    @Override
    public void updatePropertiesTemplate(Properties properties) {
        super.updatePropertiesTemplate(properties);
        properties.setProperty(URL, "http://pathToJenkins/computer/");
    }
}
