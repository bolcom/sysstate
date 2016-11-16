package nl.unionsoft.sysstate.common.extending;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;

public abstract class InstanceStateResolver implements StateResolver {

    private final Logger log = LoggerFactory.getLogger(InstanceStateResolver.class);

    protected InstanceLinkLogic instanceLinkLogic;
    protected InstanceLogic instanceLogic;
    protected EnvironmentLogic environmentLogic;
    
    public InstanceStateResolver(InstanceLinkLogic instanceLinkLogic, InstanceLogic instanceLogic, EnvironmentLogic environmentLogic) {
        this.instanceLinkLogic = instanceLinkLogic;
        this.instanceLogic = instanceLogic;
        this.environmentLogic = environmentLogic;
    }

    @Override
    public void setState(InstanceDto instance, StateDto state) {
        List<InstanceDto> childInstances = instanceLinkLogic.getInstanceLinks(instance.getId())
                .stream().filter(il -> il.getDirection().equals(Direction.OUTGOING) && il.getName().equals("child"))
                .map(il -> instanceLogic.getInstance(il.getInstanceToId()))
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                .collect(Collectors.toList());

        List<InstanceDto> updatedInstances = createOrUpdateInstances(instance, childInstances);
        updatedInstances.stream().forEach( child -> instanceLinkLogic.link(instance.getId(),child.getId(), "child"));
        deleteNoLongerValidInstances(updatedInstances, childInstances);
        deleteEnvironmentsWithoutInstances();
        state.setState(StateType.STABLE);

    }

    private void deleteEnvironmentsWithoutInstances() {
        log.info("Deleting Environments without instances...");
        environmentLogic.getEnvironments().stream().forEach(environment -> {
            if (instanceLogic.getInstancesForEnvironment(environment.getId()).isEmpty()) {
                log.info("Deleting environment with id [${environment.id}] since it is no longer used.");
                environmentLogic.delete(environment.getId());
            }
        });

    }

    private void deleteNoLongerValidInstances(List<InstanceDto> updatedInstances, List<InstanceDto> children) {
        log.info("Removing children that are not in the list of updatedInstances...");
        List<Long> validInstanceIds = updatedInstances.stream().map(i -> i.getId()).collect(Collectors.toList());
        children.stream().forEach(i -> {
            if (!validInstanceIds.contains(i.getId())) {
                log.info("Deleting instance with id [${i.id}] sinze it is no longer used.");
                instanceLogic.delete(i.getId());
            }
        });
    }

    @Override
    public abstract String generateHomePageUrl(InstanceDto instance);

    public abstract List<InstanceDto> createOrUpdateInstances(InstanceDto parent, List<InstanceDto> children);

}
