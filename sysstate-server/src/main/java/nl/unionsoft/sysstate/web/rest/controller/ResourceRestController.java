package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import nl.unionsoft.commons.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.ResourceDto;
import nl.unionsoft.sysstate.common.logic.ResourceLogic;
import nl.unionsoft.sysstate.sysstate_1_0.Property;
import nl.unionsoft.sysstate.sysstate_1_0.Resource;
import nl.unionsoft.sysstate.sysstate_1_0.ResourceList;
import nl.unionsoft.sysstate.web.rest.converter.ResourceConverter;

@Controller()
public class ResourceRestController {

    @Inject
    private ResourceLogic resourceLogic;

    @Inject
    private ResourceConverter projectConverter;

    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    public ResourceList getProjects() {
        List<Resource> projects = ListConverter.convert(projectConverter, resourceLogic.getResources());
        ResourceList resourceList = new ResourceList();
        resourceList.getResources().addAll(projects);
        return resourceList;
    }

    @RequestMapping(value = "/resource", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Resource resource) {
        ResourceDto dto = new ResourceDto();
        dto.setName(resource.getName());
        dto.setManager(resource.getManager());
        dto.setConfiguration(resource.getProperties().stream().collect(Collectors.toMap(Property::getKey, Property::getValue)));
        resourceLogic.createOrUpdate(dto);
    }

    @RequestMapping(value = "/resource/{resourceManager}/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("resourceManager") final String resourceManager, @PathVariable("name") final String name) {
        resourceLogic.deleteResource(resourceManager, name);
    }
}
