package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.ErrorMessage;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;
import nl.unionsoft.sysstate.sysstate_1_0.Property;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller()
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class InstanceRestController {

    private static final Logger log = LoggerFactory.getLogger(InstanceRestController.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("pushStateLogic")
    private PushStateLogic pushStateLogic;

    @Inject
    @Named("restInstanceConverter")
    private Converter<Instance, InstanceDto> instanceConverter;

    @RequestMapping(value = "/instance", method = RequestMethod.GET)
    public InstanceList getInstances() {
        List<Instance> instances = ListConverter.convert(instanceConverter, instanceLogic.getInstances());
        InstanceList instanceList = new InstanceList();
        instanceList.getInstances().addAll(instances);
        return instanceList;
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    public Instance getInstance(@PathVariable("instanceId") final Long instanceId) {
        return instanceConverter.convert(instanceLogic.getInstance(instanceId, false));
    }

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public Instance create(@RequestBody Instance instance) {
        if (instance.getId() != null) {
            throw new IllegalStateException("Instance id should be null for create");
        }
        InstanceDto instanceDto = convert(instance);
        Long id = instanceLogic.createOrUpdateInstance(instanceDto);
        return instanceConverter.convert(instanceLogic.getInstance(id));
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void update(@PathVariable("instanceId") final Long instanceId, @RequestBody Instance instance) {

        if (!instance.getId().equals(instanceId)) {
            throw new IllegalStateException("instanceId in url and instanceId in instance do not match.");
        }
        InstanceDto instanceDto = convert(instance);
        instanceLogic.createOrUpdateInstance(instanceDto);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Exception ex){
       log.error("Handling Exception", ex);
       ErrorMessage errorMessage = new ErrorMessage();
       errorMessage.setMessage(ex.getMessage());
       return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.BAD_REQUEST);
    }
    
    public InstanceDto convert(Instance instance) {
        InstanceDto instanceDto = new InstanceDto();
        instanceDto.setEnabled(instance.isEnabled());
        instanceDto.setHomepageUrl(instance.getHomepageUrl());
        instanceDto.setId(instance.getId());
        instanceDto.setName(instance.getName());
        instanceDto.setPluginClass(instance.getPlugin());
        instanceDto.setProjectEnvironment(getProjectEnvironment(instance));
        instanceDto.setConfiguration(instance.getProperties().stream().collect(Collectors.toMap(Property::getKey, Property::getValue)));
        return instanceDto;
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("instanceId") final Long instanceId) {
        instanceLogic.delete(instanceId);
    }

    private ProjectEnvironmentDto getProjectEnvironment(Instance instance) {

        ProjectEnvironment projectEnvironment = instance.getProjectEnvironment();

        if (projectEnvironment == null) {
            throw new IllegalArgumentException("ProjectEnvironment is required!");
        }
        if (isIdSet(projectEnvironment.getId())) {
            return projectEnvironmentLogic.getProjectEnvironment(projectEnvironment.getId());
        }

        Project project = projectEnvironment.getProject();
        Environment environment = projectEnvironment.getEnvironment();

        if (project == null || environment == null) {
            throw new IllegalArgumentException("ProjectEnvironment should have both a project and an environment!");
        }
        
        if (isIdSet(project.getId()) && isIdSet(environment.getId())) {
            return projectEnvironmentLogic.getProjectEnvironment(project.getId(), environment.getId());
        }

        if (StringUtils.isNotEmpty(project.getName()) && StringUtils.isNotEmpty(environment.getName())) {
            return projectEnvironmentLogic.getProjectEnvironment(project.getName(), environment.getName());
        }

        throw new IllegalArgumentException("Unable to determine projectEnvironment from instance [" + instance + "].");
    }

    private boolean isIdSet(Long id) {
        return id != null && id > 0;
    }

}
