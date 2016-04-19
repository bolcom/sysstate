package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;
import nl.unionsoft.sysstate.web.rest.converter.InstanceConverter;
import nl.unionsoft.sysstate.web.rest.converter.ProjectEnvironmentConverter;
import nl.unionsoft.sysstate.web.rest.converter.StateConverter;

@Controller()
@RequestMapping()
public class ProjectEnvironmentRestController {
    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("restInstanceConverter")
    private InstanceConverter instanceConverter;

    @Inject
    @Named("restStateConverter")
    private StateConverter stateConverter;

    @Inject
    @Named("restProjectEnvironmentConverter")
    private ProjectEnvironmentConverter projectEnvironmentConverter;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @RequestMapping(value = "/projectenvironment", method = RequestMethod.GET)
    public ProjectEnvironment getProjectEnvironment(@RequestParam("projectName") String projectName, @RequestParam("environmentName") String environmentName,
            @RequestParam(value = "state", required = false, defaultValue = "CACHED") StateBehaviour stateBehaviour) {

        ProjectEnvironmentDto projectEnvironmentDto = projectEnvironmentLogic.getProjectEnvironment(projectName, environmentName);

        if (projectEnvironmentDto == null) {
            return new ProjectEnvironment();
        }

        // @formatter:off
        List<Instance> instances = instanceLogic.getInstancesForProjectEnvironment(projectEnvironmentDto.getId()).stream().map(dto -> {
            Instance instance = instanceConverter.convert(dto, new Integer[] {});
            StateDto instanceState = stateLogic.getLastStateForInstance(dto, stateBehaviour);
            instance.setState(stateConverter.convert(instanceState));
            //FIXME: Don't modify projectEnvironment here... 
            projectEnvironmentDto.setState(StateType.transfer(projectEnvironmentDto.getState(), instanceState.getState()));
            return instance;
        }).collect(Collectors.toList());
        // @formatter:on

        ProjectEnvironment projectEnvironment = projectEnvironmentConverter.convert(projectEnvironmentDto);

        projectEnvironment.getInstances().addAll(instances);
        return projectEnvironment;
    }

}
