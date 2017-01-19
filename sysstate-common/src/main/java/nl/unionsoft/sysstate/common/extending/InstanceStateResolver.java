package nl.unionsoft.sysstate.common.extending;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.RelationalInstanceLogic;

public abstract class InstanceStateResolver implements StateResolver {

    private final static Logger log = LoggerFactory.getLogger(InstanceStateResolver.class);

    private final RelationalInstanceLogic relationalInstanceLogic;

    public InstanceStateResolver(RelationalInstanceLogic relationalInstanceLogic) {
        this.relationalInstanceLogic = relationalInstanceLogic;
    }

    @Override
    public void setState(InstanceDto parent, StateDto state) {
        List<InstanceDto> childInstances = relationalInstanceLogic.getChildInstances(parent);

        List<InstanceDto> instances = generateInstances(parent);

        instances.stream().forEach(instance -> {
            log.debug("Optionally setting id from existing child for instance [{}]", instance);
            relationalInstanceLogic.findChildInstanceId(childInstances, instance.getReference())
                    .ifPresent(childInstanceId -> instance.setId(childInstanceId));

            postConfigure(instance, parent);

            relationalInstanceLogic.createOrUpdateInstance(instance, parent);

        });

        relationalInstanceLogic.deleteNoLongerValidInstances(instances, childInstances);
        state.setState(StateType.STABLE);
        state.setDescription("OK");
    }

    private void postConfigure(InstanceDto instance, InstanceDto parent) {
        log.debug("Post configuring instance [{}]", instance);
        Map<String, String> configuration = parent.getConfiguration();
        instance.setRefreshTimeout(Integer.valueOf(StringUtils.defaultString(configuration.get("child_refreshTimeout"), "60000")));
    }

    @Override
    public abstract String generateHomePageUrl(InstanceDto instance);

    public abstract List<InstanceDto> generateInstances(InstanceDto parent);

}
