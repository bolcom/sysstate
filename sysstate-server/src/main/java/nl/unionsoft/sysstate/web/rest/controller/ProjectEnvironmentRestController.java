package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import org.springframework.http.MediaType;
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
    private Converter<Instance, InstanceDto> instanceConverter;

    @Inject
    @Named("restProjectEnvironmentConverter")
    private Converter<ProjectEnvironment, ProjectEnvironmentDto> projectEnvironmentConverter;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @RequestMapping(value = "/projectenvironment", method = RequestMethod.GET)
    public ProjectEnvironment getProjectEnvironment(@RequestParam("projectName") String projectName, @RequestParam("environmentName") String environmentName,
            @RequestParam(value = "directState", required = false, defaultValue = "false") boolean directState) {
        ProjectEnvironment projectEnvironment = projectEnvironmentConverter
                .convert(projectEnvironmentLogic.getProjectEnvironment(projectName, environmentName));
        
        if (projectEnvironment == null){
            throw new IllegalStateException("No projectEnvironment could be found for projectName [" + projectName + "] and environmentName [" + environmentName + "].");
        }
        
        List<InstanceDto> instances = instanceLogic.getInstancesForProjectEnvironment(projectEnvironment.getId());
        if (directState) {
            instances.forEach(instance -> {
                instance.setState(stateLogic.requestStateForInstance(instance));
            });
        }
        projectEnvironment.getInstances().addAll(ListConverter.convert(instanceConverter, instances));
        return projectEnvironment;
    }
}
