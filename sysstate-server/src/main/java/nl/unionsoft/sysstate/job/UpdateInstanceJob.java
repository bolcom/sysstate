package nl.unionsoft.sysstate.job;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

public class UpdateInstanceJob implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateInstanceJob.class);

    private final InstanceLogic instanceLogic;
    private final StateLogic stateLogic;

    private final long instanceId;

    public UpdateInstanceJob(InstanceLogic instanceLogic, StateLogic stateLogic, long instanceId) {
        super();
        this.instanceLogic = instanceLogic;
        this.stateLogic = stateLogic;
        this.instanceId = instanceId;
    }

    @Override
    public void run() {

        Optional<InstanceDto> optInstance = instanceLogic.getInstance(instanceId);
        if (!optInstance.isPresent()) {
            LOG.error("Instance with instanceId [{}] (no longer) exists!", instanceId);
            return;
        }

        InstanceDto instance = optInstance.get();
        LOG.trace("Starting job with instance '{}'", instance);
        try {
            final StateDto state = stateLogic.requestStateForInstance(instance);
            stateLogic.createOrUpdate(state);
            LOG.trace("job for instance '{}' completed, state: {}", instance, state);
        } catch (final Exception e) {
            LOG.warn("job for instance '{}' failed, caught Exception!", instance, e);
        }

    }

}
