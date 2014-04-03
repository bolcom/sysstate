package nl.unionsoft.sysstate.logic.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.NotifierLogic;

@Service("notifierLogic")
public class NotifierLogicImpl implements NotifierLogic {

    private static final Logger LOG = LoggerFactory.getLogger(NotifierLogicImpl.class);

    public void notify(InstanceDto instance, StateDto state, StateDto lastState) {
        LOG.debug("Handle notifications for instance '" + instance.getId() + "', new state is '" + state + "', old state is '" + lastState + "'.");
    }

}
