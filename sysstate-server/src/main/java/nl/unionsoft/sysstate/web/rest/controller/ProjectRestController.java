package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.sysstate_1_0.ErrorMessage;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectList;

import org.apache.commons.collections.CollectionUtils;
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
public class ProjectRestController {

    private static final Logger log = LoggerFactory.getLogger(ProjectRestController.class);

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("restProjectConverter")
    private Converter<Project, ProjectDto> projectConverter;

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public ProjectList getProjects() {
        List<Project> projects = ListConverter.convert(projectConverter, projectLogic.getProjects());
        ProjectList projectList = new ProjectList();
        projectList.getProjects().addAll(projects);
        return projectList;
    }

    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public Project getProject(@PathVariable("projectId") final Long projectId) {
        return projectConverter.convert(projectLogic.getProject(projectId));
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public Project create(@RequestBody Project project) {
        if (project.getId() != null) {
            throw new IllegalStateException("Project id should be null for create");
        }
        ProjectDto projectDto = convert(project);
        Long id = projectLogic.createOrUpdateProject(projectDto);
        return projectConverter.convert(projectLogic.getProject(id));
    }

    private ProjectDto convert(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setOrder(project.getOrder());
        projectDto.setTags(StringUtils.join(project.getTags(), " "));
        return projectDto;
    }

    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("projectId") final Long projectId, @RequestBody Project project) {

        if (!project.getId().equals(projectId)) {
            throw new IllegalStateException("projectId in url and projectId in project do not match.");
        }
        ProjectDto projectDto = convert(project);
        projectLogic.createOrUpdateProject(projectDto);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        return ErrorMessageCreator.createMessageFromException(ex);
    }

}
