package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
public class InstanceRestController {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceRestController.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("pushStateLogic")
    private PushStateLogic pushStateLogic;

    @RequestMapping(value = "/instance/{instanceId}/state/", method = RequestMethod.GET)
    public State stateForInstance(@PathVariable("instanceId") final Long instanceId) {
        final State state = new State();
        return state;
    }

    @RequestMapping(value = "/instance/{environment}/{project}/{instance}/state/direct", method = RequestMethod.GET)
    //@formatter:off
    public State stateForInstanceNoCache(
            @PathVariable("environment") final String environment, 
            @PathVariable("project") final String project,
            @PathVariable("instance") final String instanceName) {
        //@formatter:on
        final List<InstanceDto> instances = instanceLogic.getInstancesForPrefixes(project, environment);
        final InstanceDto selected = getInstanceWithName(instanceName, instances);
        LOG.info("Selected instance for env '{}', prj '{}' and instanceName is: {}", new Object[] { environment, project, instanceName, selected });
        final State state = new State();
        if (selected != null) {
            final StateDto dto = stateLogic.requestStateForInstance(selected);
            if (dto != null) {
                state.setDescription(dto.getDescription());
                state.setState(ObjectUtils.toString(dto.getState()));
            }
        }
        return state;
    }


    @RequestMapping(value = "/instance/{environment}/{project}/{instance}/state/push", method = RequestMethod.POST)
    //@formatter:off
    public void pushState(
            @PathVariable("environment") final String environment, 
            @PathVariable("project") final String project,
            @PathVariable("instance") final String instanceName, 
            @RequestParam(value="state", required = true) final StateType stateType,
            @RequestParam(value="description", required = false) final String description,
            @RequestParam(value="message", required=false) final String message,
            @RequestParam(value="responseTime", required=false, defaultValue="0") final Long responseTime) {
        //@formatter:on
        final List<InstanceDto> instances = instanceLogic.getInstancesForPrefixes(project, environment);
        final InstanceDto selected = getInstanceWithName(instanceName, instances);
        LOG.info("Selected instance for env '{}', prj '{}' and instanceName is: {}", new Object[] { environment, project, instanceName, selected });
        if (selected != null) {
            final StateDto state = new StateDto();

            state.setState(stateType);
            state.setMessage(message);
            state.setResponseTime(responseTime);
            state.setDescription(description);
            pushStateLogic.push(selected.getId(), state);
        }
    }

    private InstanceDto getInstanceWithName(final String instanceName, List<InstanceDto> instances) {
        InstanceDto selected = null;
        for (final InstanceDto instance : instances) {
            if (StringUtils.equalsIgnoreCase(instance.getName(), instanceName)) {
                if (selected != null) {
                    throw new IllegalStateException("Multiple instances found for given instanceName");
                }
                selected = instance;
            }
        }
        return selected;
    }

    @RequestMapping(value = "/instance/{instanceId}/state/direct", method = RequestMethod.GET)
    public State stateForInstanceNoCache(@PathVariable("instanceId") final Long instanceId) {
        final State state = new State();
        final StateDto dto = stateLogic.requestStateForInstance(instanceLogic.getInstance(instanceId));
        if (dto != null) {
            state.setDescription(dto.getDescription());
            state.setState(ObjectUtils.toString(dto.getState()));
        }
        return state;
    }

}
