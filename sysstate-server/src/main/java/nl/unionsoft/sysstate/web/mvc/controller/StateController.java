package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class StateController {

    @Inject
    @Named("stateLogic")
    private StateLogic stateLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;
    
    @RequestMapping(value = "/state/instance/{instanceId}/message", method = RequestMethod.GET)
    public ModelAndView message(@PathVariable("instanceId") Long instanceId, final HttpSession session) {
        final ModelAndView modelAndView = new ModelAndView("message-clear");
        final StateDto state = stateLogic.getLastStateForInstance(instanceLogic.getInstance(instanceId));
        modelAndView.addObject("message", state.getMessage());
        return modelAndView;
    }

}
