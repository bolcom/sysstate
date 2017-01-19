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
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic;
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.common.logic.TemplateLogic
import nl.unionsoft.sysstate.plugins.groovy.basic.XPathStateResolver
import nl.unionsoft.sysstate.plugins.http.HttpStateResolverImpl
import nl.unionsoft.sysstate.plugins.http.HttpConstants

@Named("consulXpathInstanceResolver")
class ConsulXpathInstanceResolver extends AbstractConsulPatternInstanceResolver{

    private final TemplateLogic templateLogic;

    @Inject
    public ConsulXpathInstanceResolver(RelationalInstanceLogic relationalInstanceLogic, ResourceLogic resourceLogic, TemplateLogic templateLogic){
        super(relationalInstanceLogic, resourceLogic)
        this.templateLogic = templateLogic;
    }

    @Override
    public void configure(InstanceDto instance, InstanceDto parent) {
        def url = getUrl(parent, instance.projectEnvironment.project, instance.projectEnvironment.environment)
        instance.pluginClass = "xPathStateResolver"
        instance.homepageUrl= url;
        instance.configuration[HttpStateResolverImpl.URL] = url;
        instance.configuration[XPathStateResolver.XPATH] = parent.configuration["child_${XPathStateResolver.XPATH}"]
        instance.configuration[XPathStateResolver.PREDEFINED_XPATH] = parent.configuration["child_${XPathStateResolver.PREDEFINED_XPATH}"]
        instance.configuration[HttpConstants.HTTP_CLIENT_ID] = parent.configuration["child_${HttpConstants.HTTP_CLIENT_ID}"]
        instance.tags = parent.configuration["child_tags"] ? parent.configuration['child_tags'] : 'consul'
    }

    private String getUrl(InstanceDto parent, ProjectDto project, EnvironmentDto environment) {
        def urlTemplate = parent.configuration['child_urlTemplate']
        assert urlTemplate, "No urlTemplate defined"
        StringWriter stringWriter = new StringWriter();
        templateLogic.writeTemplate(urlTemplate, ['project':project, 'environment' : environment], stringWriter)
        return stringWriter.toString()
    }
}
