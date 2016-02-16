package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ConverterWithConfig;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.enums.StateBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    private ConverterWithConfig<Instance, InstanceDto, Integer[]> instanceConverter;

    @Inject
    @Named("restProjectEnvironmentConverter")
    private Converter<ProjectEnvironment, ProjectEnvironmentDto> projectEnvironmentConverter;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @RequestMapping(value = "/projectenvironment", method = RequestMethod.GET)
    public ProjectEnvironment getProjectEnvironment(
            @RequestParam("projectName") String projectName, 
            @RequestParam("environmentName") String environmentName,
            @RequestParam(value = "state", required = false, defaultValue = "CACHED") StateBehaviour state) {
        
        ProjectEnvironmentDto projectEnvironmentDto = projectEnvironmentLogic.getProjectEnvironment(projectName, environmentName);

        if (projectEnvironmentDto == null) {
            return new ProjectEnvironment();
        }

        List<InstanceDto> instances = instanceLogic.getInstancesForProjectEnvironment(projectEnvironmentDto.getId());
        switch (state) {
            case DIRECT:
                instances.forEach(instance -> {
                    instance.setState(stateLogic.requestStateForInstance(instance));
                });
                break;
            case CACHED:
                instances.forEach(instance -> {
                    instance.setState(stateLogic.getLastStateForInstance(instance.getId()));
                });
                break;
            default:
                throw new IllegalStateException("Unsupported StateBehaviour [" + state + "]");

        }
        
        instances.stream().forEach(instance -> {
            projectEnvironmentDto.setState(StateType.transfer(projectEnvironmentDto.getState(), instance.getState().getState()));
        });

        ProjectEnvironment projectEnvironment = projectEnvironmentConverter.convert(projectEnvironmentDto);
        projectEnvironment.getInstances().addAll(ListConverter.convert(instanceConverter, instances, new Integer[] {}));
        return projectEnvironment;
    }

}
