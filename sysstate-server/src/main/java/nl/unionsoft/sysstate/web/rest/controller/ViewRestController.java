package nl.unionsoft.sysstate.web.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller()
public class ViewRestController {
    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    @Inject
    @Named("ecoSystemLogic")
    private EcoSystemLogic ecoSystemLogic;

    @RequestMapping(value = "/view/{viewId}/states", method = RequestMethod.GET)
    public List<State> statesForView(@PathVariable("viewId") Long viewId) {
        List<State> states = new ArrayList<State>();
        final ViewDto view = viewLogic.getView(viewId);
        if (view != null) {
            ViewResultDto viewResults = ecoSystemLogic.getEcoSystem(view);
            for (InstanceDto instance : viewResults.getInstances()) {
                StateDto stateDto = instance.getState();
                State state = new State();
                state.setId(stateDto.getId());
                state.setState(stateDto.getState().toString());
                states.add(state);
            }
        }
        return states;
    }
}
