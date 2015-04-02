package nl.unionsoft.sysstate.web.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.PushStateLogic;
import nl.unionsoft.sysstate.logic.StateLogic;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
public class InstanceRestController {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceRestController.class);

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("pushStateLogic")
    private PushStateLogic pushStateLogic;

    @Inject
    @Named("restInstanceConverter")
    private  Converter<Instance, InstanceDto> instanceConverter;
    
    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    public Instance getInstance(@PathVariable("instanceId") final Long instanceId) {
        return instanceConverter.convert(instanceLogic.getInstance(instanceId, false));
    }
    

}
