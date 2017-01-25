package nl.unionsoft.sysstate.plugins.groovy.marathon

import javax.inject.Inject;
import javax.inject.Named

import nl.unionsoft.sysstate.common.dto.InstanceDto
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic
import nl.unionsoft.sysstate.common.logic.InstanceLogic
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic;
import nl.unionsoft.sysstate.common.logic.ResourceLogic
import nl.unionsoft.sysstate.common.logic.TemplateLogic
@Named("marathonAppInstanceResolver")
class MarathonAppInstanceResolver extends AbstractMarathonPatternInstanceResolver{

    @Inject
    public MarathonAppInstanceResolver(RelationalInstanceLogic relationalInstanceLogic,ResourceLogic resourceLogic, TemplateLogic templateLogic) {
        super(relationalInstanceLogic, resourceLogic, templateLogic)
    }

    @Override
    public void configure(def app, InstanceDto instance, InstanceDto parent) {
        instance.pluginClass = "marathonAppStateResolver"
        instance.configuration['applicationPath'] = app['id']
        instance.configuration['ignoreHealthStatus'] = parent.configuration['child_ignoreHealthStatus'] ?: 'false'
        instance.configuration['serverUrl'] = parent.configuration['child_serverUrl'] ?: parent.configuration['serverUrl']
        instance.configuration['userName'] = parent.configuration['child_userName'] ?: parent.configuration['userName']
        instance.configuration['password'] = parent.configuration['child_password'] ?: parent.configuration['password']
        instance.tags = parent.configuration["child_tags"] ? parent.configuration['child_tags'] : 'marathon'
    }
}
