package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic;

@Service("relationalInstanceLogicImpl")
public class RelationalInstanceLogicImpl implements RelationalInstanceLogic {

    private final static Logger log = LoggerFactory.getLogger(RelationalInstanceLogicImpl.class);
    
    private final InstanceLinkLogic instanceLinkLogic;
    private final InstanceLogic instanceLogic;
    private final ProjectLogic projectLogic;
    private final EnvironmentLogic environmentLogic;

    @Inject
    public RelationalInstanceLogicImpl(InstanceLinkLogic instanceLinkLogic, InstanceLogic instanceLogic, ProjectLogic projectLogic,
            EnvironmentLogic environmentLogic) {

        this.instanceLinkLogic = instanceLinkLogic;
        this.instanceLogic = instanceLogic;
        this.projectLogic = projectLogic;
        this.environmentLogic = environmentLogic;
    }

    @Override
    public List<InstanceDto> getChildInstances(InstanceDto parent) {
        return instanceLinkLogic.getInstanceLinks(parent.getId())
                .stream().filter(il -> il.getDirection().equals(Direction.OUTGOING) && il.getName().equals("child"))
                .map(il -> instanceLogic.getInstance(il.getInstanceToId()))
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                .collect(Collectors.toList());

    }

    public Optional<Long> findChildInstanceId(List<InstanceDto> childInstances, String reference) {
        return childInstances.stream()
                .filter(child -> child.getReference().equals(reference))
                .map(InstanceDto::getId)
                .findFirst();

    }

    @Override
    public void createOrUpdateInstance(InstanceDto child, InstanceDto parent) {
        log.debug("Persisting & linking instance [{}]", child);
        projectLogic.findOrCreateProject(child.getProjectEnvironment().getProject().getName());
        environmentLogic.findOrCreateEnvironment(child.getProjectEnvironment().getEnvironment().getName());
        instanceLogic.createOrUpdateInstance(child);
        instanceLinkLogic.link(parent.getId(), child.getId(), "child");

    }
    @Override
    public void deleteNoLongerValidInstances(List<InstanceDto> updatedInstances, List<InstanceDto> children) {
        
        
        log.info("Removing children that are not in the list of updatedInstances...");
        List<Long> validInstanceIds = updatedInstances.stream().map(i -> i.getId()).collect(Collectors.toList());
        children.stream().forEach(i -> {
            if (!validInstanceIds.contains(i.getId())) {
                log.info("Deleting instance with id [${i.id}] sinze it is no longer used.");
                instanceLogic.delete(i.getId());
            }
        });
    }


}
