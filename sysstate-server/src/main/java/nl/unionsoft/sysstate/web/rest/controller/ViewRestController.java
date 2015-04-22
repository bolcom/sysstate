package nl.unionsoft.sysstate.web.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.logic.EcoSystemLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.ViewLogic;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.apache.commons.lang.StringUtils;
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

    @Inject
    @Named("restEcoSystemConverter")
    private Converter<EcoSystem, ViewResultDto> ecoSystemConverter;

    @RequestMapping(value = "/view/{viewId}/ecosystem", method = RequestMethod.GET)
    public EcoSystem ecosystemForView(@PathVariable("viewId") Long viewId) {

        if (viewId == 0L) {
            Properties viewConfiguration = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);
            String defaultViewProperty = viewConfiguration.getProperty("defaultView");
            ViewDto view = viewLogic.getBasicView();
            if (StringUtils.isNotEmpty(defaultViewProperty)) {
                view = viewLogic.getView(Long.valueOf(defaultViewProperty));
            }
            return ecoSystemConverter.convert(ecoSystemLogic.getEcoSystem(view));
        } else {
            final ViewDto view = viewLogic.getView(viewId);
            return ecoSystemConverter.convert(ecoSystemLogic.getEcoSystem(view));
        }
    }

}
