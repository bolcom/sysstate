package nl.unionsoft.sysstate.plugins.groovy.consul

import javax.inject.Inject
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.EnvironmentDto
import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.dto.ProjectDto
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.ProjectLogic
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.common.logic.TemplateLogic
import nl.unionsoft.sysstate.plugins.groovy.basic.XPathStateResolver
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverImpl

import org.apache.commons.lang.text.StrSubstitutor
@Named("consulXpathInstanceResolver")
class ConsulXpathInstanceResolver extends ConsulPatternInstanceResolver{

    private final TemplateLogic templateLogic;

    @Inject
    public ConsulXpathInstanceResolver(ResourceLogic resourceLogic, ProjectLogic projectLogic, EnvironmentLogic environmentLogic, InstanceLogic instanceLogic, InstanceLinkLogic instanceLinkLogic, TemplateLogic templateLogic){
        super(resourceLogic, projectLogic, environmentLogic, instanceLogic, instanceLinkLogic)
        this.templateLogic = templateLogic;
    }

    @Override
    public String getType() {
        return "xPathStateResolver";
    }

    @Override
    public void configure(InstanceDto instance, ProjectDto project, EnvironmentDto environment, InstanceDto parent) {
        def urlTemplate = parent.configuration['urlTemplate']
        assert urlTemplate, "No urlTemplate defined"
        StringWriter stringWriter = new StringWriter();
        templateLogic.writeTemplate(urlTemplate, ['project':project, 'environment' : environment], stringWriter)
        def url = stringWriter.toString()
        instance.homepageUrl= url;
        instance.configuration[HttpStateResolverImpl.URL] = url;
        instance.configuration[XPathStateResolver.XPATH] = parent.configuration[XPathStateResolver.XPATH]
        instance.configuration[XPathStateResolver.PREDEFINED_XPATH] = parent.configuration[XPathStateResolver.PREDEFINED_XPATH]
        instance.tags = parent.configuration["tags"] ? parent.configuration['tags'] : 'consul'
    }
}
