package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.MessageLogic;

@Controller()
public class InstanceMultiController {
    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @RequestMapping(value = "/instance/multi/{action}", method = RequestMethod.POST)
    public ModelAndView multi(@RequestParam("instanceId") Long[] instanceIds, @PathVariable("action") String action) {

        if (instanceIds != null) {
            for (final Long instanceId : instanceIds) {

                Optional<InstanceDto> optInstance = instanceLogic.getInstance(instanceId);
                if (optInstance.isPresent()) {
                    final InstanceDto instance = optInstance.get();
                    if (StringUtils.equalsIgnoreCase("disable", action)) {
                        instance.setEnabled(false);
                        messageLogic.addUserMessage(new MessageDto("Instance with id '" + instanceId + "' disabled!", MessageDto.GREEN));
                    } else if (StringUtils.equalsIgnoreCase("enable", action)) {
                        instance.setEnabled(true);
                        messageLogic.addUserMessage(new MessageDto("Instance with id '" + instanceId + "' enabled!", MessageDto.GREEN));
                    }
                    instanceLogic.createOrUpdateInstance(instance);
                    instanceLogic.queueForUpdate(instanceId);
                }
            }
        }

        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/instance/multi/refresh", method = RequestMethod.POST)
    public ModelAndView refresh(@RequestParam("instanceId") Long[] instanceIds) {
        if (instanceIds != null) {
            for (final Long instanceId : instanceIds) {
                instanceLogic.queueForUpdate(instanceId);
                messageLogic.addUserMessage(new MessageDto("Instance with id '" + instanceId + "' queued for update!", MessageDto.GREEN));
            }
        }
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/instance/multi/delete", method = RequestMethod.POST)
    public ModelAndView delete(@RequestParam("instanceId") Long[] instanceIds) {

        final List<InstanceDto> instancesToDelete = new ArrayList<InstanceDto>();
        final ModelAndView modelAndView = new ModelAndView("multi-delete-instance-manager");
        if (instanceIds != null) {
            for (final Long instanceId : instanceIds) {
                Optional<InstanceDto> optInstance = instanceLogic.getInstance(instanceId);
                if (optInstance.isPresent()) {
                    final InstanceDto instance = optInstance.get();
                    instancesToDelete.add(instance);
                }
                
            }
        }
        modelAndView.addObject("instances", instancesToDelete);
        return modelAndView;
    }

    @RequestMapping(value = "/instance/multi/delete/confirm", method = RequestMethod.POST)
    public ModelAndView deleteConfirm(@RequestParam("instanceId") Long[] instanceIds) {
        for (final Long instanceId : instanceIds) {
            instanceLogic.delete(instanceId);
        }
        messageLogic.addUserMessage(new MessageDto("Deleted '" + instanceIds.length + "' instances...", MessageDto.GREEN));
        return new ModelAndView("redirect:/filter/index.html");
    }

}
