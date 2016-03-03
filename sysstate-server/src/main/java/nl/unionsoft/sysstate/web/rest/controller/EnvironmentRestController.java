package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;

import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.EnvironmentList;
import nl.unionsoft.sysstate.sysstate_1_0.ErrorMessage;

@Controller()
public class EnvironmentRestController {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentRestController.class);

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("restEnvironmentConverter")
    private Converter<Environment, EnvironmentDto> environmentConverter;

    @RequestMapping(value = "/environment", method = RequestMethod.GET)
    public EnvironmentList getEnvironments() {
        List<Environment> environments = ListConverter.convert(environmentConverter, environmentLogic.getEnvironments());
        EnvironmentList environmentList = new EnvironmentList();
        environmentList.getEnvironments().addAll(environments);
        return environmentList;
    }

    @RequestMapping(value = "/environment/{environmentId}", method = RequestMethod.GET)
    public Environment getEnvironment(@PathVariable("environmentId") final Long environmentId) {
        return environmentConverter.convert(environmentLogic.getEnvironment(environmentId));
    }

    @RequestMapping(value = "/environment/{environmentId}", method = RequestMethod.DELETE)
    public void deleteEnvironment(@PathVariable("environmentId") final Long environmentId) {
        environmentLogic.delete(environmentId);
    }

    @RequestMapping(value = "/environment", method = RequestMethod.POST)
    public Environment create(@RequestBody Environment environment) {
        if (environment.getId() != null) {
            throw new IllegalStateException("Environment id should be null for create");
        }
        EnvironmentDto environmentDto = convert(environment);
        Long id = environmentLogic.createOrUpdate(environmentDto);
        return environmentConverter.convert(environmentLogic.getEnvironment(id));
    }

    private EnvironmentDto convert(Environment environment) {
        EnvironmentDto environmentDto = new EnvironmentDto();
        environmentDto.setId(environment.getId());
        environmentDto.setName(environment.getName());
        environmentDto.setOrder(environment.getOrder());
        environmentDto.setTags(StringUtils.join(environment.getTags(), " "));
        return environmentDto;
    }

    @RequestMapping(value = "/environment/{environmentId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("environmentId") final Long environmentId, @RequestBody Environment environment) {

        if (!environment.getId().equals(environmentId)) {
            throw new IllegalStateException("environmentId in url and environmentId in environment do not match.");
        }
        EnvironmentDto environmentDto = convert(environment);
        environmentLogic.createOrUpdate(environmentDto);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        return ErrorMessageCreator.createMessageFromException(ex);
    }

}
