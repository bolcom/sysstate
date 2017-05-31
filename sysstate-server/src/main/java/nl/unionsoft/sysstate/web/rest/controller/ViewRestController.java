package nl.unionsoft.sysstate.web.rest.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;

@Controller()
public class ViewRestController {
    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("viewLogic")
    private ViewLogic viewLogic;

    @Inject
    @Named("restEcoSystemConverter")
    private Converter<EcoSystem, ViewResultDto> ecoSystemConverter;

    @RequestMapping(value = "/view/{viewId}/ecosystem", method = RequestMethod.GET)
    public EcoSystem ecosystemForView(@PathVariable("viewId") String viewId) {
        Optional<ViewDto> optView = viewLogic.getView(viewId);
        if (optView.isPresent()) {
            return ecoSystemConverter.convert( viewLogic.getViewResults(optView.get()));
        }
       return new EcoSystem();
    }

}
