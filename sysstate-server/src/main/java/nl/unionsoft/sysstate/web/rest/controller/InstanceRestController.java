package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
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
import nl.unionsoft.sysstate.sysstate_1_0.State;

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

    @Inject
    @Named("restStateConverter")
    private Converter<State, StateDto> stateConverter;

    @RequestMapping(value = "/instance", method = RequestMethod.GET)
    public InstanceList getInstances() {

        // @formatter:off
        List<Instance> instances = instanceLogic.getInstances().stream().map(dto -> {
            Instance instance = instanceConverter.convert(dto);
            StateDto instanceState = stateLogic.getLastStateForInstance(dto);
            instance.setState(stateConverter.convert(instanceState));
            return instance;
        }).collect(Collectors.toList());

        InstanceList instanceList = new InstanceList();
        instanceList.getInstances().addAll(instances);
        return instanceList;
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    public Instance getInstance(@PathVariable("instanceId") final Long instanceId) {

        
        Optional<InstanceDto> optInstance = instanceLogic.getInstance(instanceId);
        if (!optInstance.isPresent()){
            throw new IllegalStateException("No instance could be found for id [" + instanceId + "]");
        }
        InstanceDto dto = optInstance.get();
        Instance instance = instanceConverter.convert(dto);
        instance.setState(stateConverter.convert(stateLogic.getLastStateForInstance(dto)));
        return instance;
    }

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public Instance create(@RequestBody Instance instance) {
        if (instance.getId() != null) {
            throw new IllegalStateException("Instance id should be null for create");
        }
        InstanceDto instanceDto = convert(instance);
        Long id = instanceLogic.createOrUpdateInstance(instanceDto);
        
        Optional<InstanceDto> optInstance = instanceLogic.getInstance(id);
        if (!optInstance.isPresent()){
            throw new IllegalStateException("Unable to fetch just created instance with id [" + id +"]");
        }
        return instanceConverter.convert(optInstance.get());
    }

    @RequestMapping(value = "/instance", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Instance instance) {

        if (instance.getId() == null && StringUtils.isEmpty(instance.getReference())) {
            throw new IllegalArgumentException("Either id or reference should be specified for update.");
        }

        InstanceDto instanceDto = convert(instance);
        instanceLogic.createOrUpdateInstance(instanceDto);

    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("instanceId") final Long instanceId) {
        instanceLogic.delete(instanceId);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        return ErrorMessageCreator.createMessageFromException(ex);
    }

    public InstanceDto convert(Instance instance) {
        InstanceDto instanceDto = new InstanceDto();
        instanceDto.setId(instance.getId());
        
        if (StringUtils.isNotEmpty(instance.getReference())){
            log.info("Reference is set to [{}], checking if an instance already exists.", instance.getReference());
            Optional<InstanceDto> optInstance = instanceLogic.getInstance(instance.getReference());
            if (optInstance.isPresent()){
                log.info("Found instance for reference [{}], we'll be updating that instead of creating a new one.", instance.getReference() );
                instanceDto = optInstance.get();
            }
        }
        instanceDto.setEnabled(instance.isEnabled());
        instanceDto.setHomepageUrl(instance.getHomepageUrl());
        instanceDto.setName(instance.getName());
        instanceDto.setPluginClass(instance.getPlugin());
        instanceDto.setReference(instance.getReference());
        instanceDto.setProjectEnvironment(getProjectEnvironment(instance));
        instanceDto.setConfiguration(instance.getProperties().stream().collect(Collectors.toMap(Property::getKey, Property::getValue)));
        instanceDto.setTags(StringUtils.join(instance.getTags(), " "));
        return instanceDto;
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
