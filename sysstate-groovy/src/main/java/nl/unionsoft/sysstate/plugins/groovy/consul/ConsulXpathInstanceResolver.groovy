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
import nl.unionsoft.sysstate.plugins.http.HttpConstants

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
        def url = getUrl(parent, project, environment)
        instance.homepageUrl= url;
        instance.configuration[HttpStateResolverImpl.URL] = url;
        instance.configuration[XPathStateResolver.XPATH] = parent.configuration["child_${XPathStateResolver.XPATH}"]
        instance.configuration[XPathStateResolver.PREDEFINED_XPATH] = parent.configuration["child_${XPathStateResolver.PREDEFINED_XPATH}"]
        instance.configuration[HttpConstants.HTTP_CLIENT_ID] = parent.configuration["child_${HttpConstants.HTTP_CLIENT_ID}"]
        instance.tags = parent.configuration["child_tags"] ? parent.configuration['child_tags'] : 'consul'
    }

    private String getUrl(InstanceDto parent, ProjectDto project, EnvironmentDto environment) {
        def urlTemplate = parent.configuration['urlTemplate']
        assert urlTemplate, "No urlTemplate defined"
        StringWriter stringWriter = new StringWriter();
        templateLogic.writeTemplate(urlTemplate, ['project':project, 'environment' : environment], stringWriter)
        return stringWriter.toString()
    }
}
