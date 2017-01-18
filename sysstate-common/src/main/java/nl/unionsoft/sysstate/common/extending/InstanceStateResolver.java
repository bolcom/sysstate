package nl.unionsoft.sysstate.common.extending;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;

public abstract class InstanceStateResolver implements StateResolver {

    private final Logger log = LoggerFactory.getLogger(InstanceStateResolver.class);

    private InstanceLinkLogic instanceLinkLogic;
    private InstanceLogic instanceLogic;

    public InstanceStateResolver(InstanceLinkLogic instanceLinkLogic, InstanceLogic instanceLogic) {
        this.instanceLinkLogic = instanceLinkLogic;
        this.instanceLogic = instanceLogic;
    }

    @Override
    public void setState(InstanceDto parent, StateDto state) {
        List<InstanceDto> childInstances = instanceLinkLogic.getInstanceLinks(parent.getId())
                .stream().filter(il -> il.getDirection().equals(Direction.OUTGOING) && il.getName().equals("child"))
                .map(il -> instanceLogic.getInstance(il.getInstanceToId()))
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                .collect(Collectors.toList());

        List<InstanceDto> instances = generateInstances(parent);

        instances.stream().forEach(instance -> {
            log.debug("Optionally setting id from existing child for instance [{}]", instance);
            childInstances.stream()
                    .filter(child -> child.getReference().equals(instance.getReference()))
                    .map(InstanceDto::getId)
                    .findFirst()
                    .ifPresent(childInstanceId -> instance.setId(childInstanceId));

            postConfigure(instance, parent);
            log.debug("Persisting & linking instance [{}]", instance);
            instanceLogic.createOrUpdateInstance(instance);
            instanceLinkLogic.link(parent.getId(), instance.getId(), "child");
        });

        deleteNoLongerValidInstances(instances, childInstances);
        state.setState(StateType.STABLE);
        state.setDescription("OK");
    }

    private void postConfigure(InstanceDto instance, InstanceDto parent) {
        log.debug("Post configuring instance [{}]", instance);
        Map<String, String> configuration = parent.getConfiguration();
        instance.setRefreshTimeout(Integer.valueOf(StringUtils.defaultString(configuration.get("child_refreshTimeout"), "60000")));
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

    public abstract List<InstanceDto> generateInstances(InstanceDto parent);

}
