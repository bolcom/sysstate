package nl.unionsoft.sysstate.plugins.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectEnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixDiscoveryImpl {

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    @Named("projectEnvironmentLogic")
    private ProjectEnvironmentLogic projectEnvironmentLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    private static final Logger LOG = LoggerFactory.getLogger(MatrixDiscoveryImpl.class);

    public Map<Long, Long> projectEnvironmentRating;

    public MatrixDiscoveryImpl() {

    }

    public void fetchMatrixValues() {
        List<EnvironmentDto> environments = environmentLogic.getEnvironments();
        for (ProjectDto project : projectLogic.getProjects()) {
            LOG.info("Cross checking project {}", project.getName());
            List<EnvironmentDto> emptyEnvironments = new ArrayList<EnvironmentDto>();
            List<InstanceDto> foundInstances = new ArrayList<InstanceDto>();
            for (EnvironmentDto environment : environments) {
                FilterDto filterDto = new FilterDto();
                filterDto.getProjects().add(project.getId());
                filterDto.getEnvironments().add(environment.getId());
                ListResponse<InstanceDto> listResponse = instanceLogic.getInstances(filterDto);
                if (listResponse.getTotalRecords() == 0) {
                    LOG.info("No results found for project and environment, adding environment {} to list of empty environments...", environment.getName());
                    emptyEnvironments.add(environment);
                } else {
                    LOG.info("Project and environment has one or more instances, adding instances to list of found instances...");
                    foundInstances.addAll(listResponse.getResults());
                }
            }
            if (!emptyEnvironments.isEmpty() && !foundInstances.isEmpty()) {
            }
        }
    }
}
