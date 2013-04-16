package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.common.list.model.ObjectRestriction;
import nl.unionsoft.common.list.model.Restriction.Rule;
import nl.unionsoft.common.list.model.Sort;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.plugins.PostWorkerPlugin;
import nl.unionsoft.sysstate.domain.Instance;
import nl.unionsoft.sysstate.domain.InstanceWorkerPluginConfig;
import nl.unionsoft.sysstate.logic.InstanceWorkerPluginLogic;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class InstanceActionController {

    @Inject
    @Named("instanceWorkerPluginLogic")
    private InstanceWorkerPluginLogic instanceWorkerPluginLogic;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    // .shtml not required
    @RequestMapping(value = "/instancenotifier/index", method = RequestMethod.GET)
    public ModelAndView list(@RequestParam(value = "firstResult", required = false) final Integer firstResult, //
            @RequestParam(value = "maxResults", required = false, defaultValue = "20") final Integer maxResults, //
            @RequestParam(value = "sort", required = false, defaultValue = "id|DESC") final String sort, @RequestParam(value = "instanceId", required = false) final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("list-instance-notifier-manager");
        final ListRequest listRequest = new ListRequest();
        listRequest.setFirstResult(firstResult);
        listRequest.setMaxResults(maxResults);
        if (StringUtils.isNotEmpty(sort)) {
            listRequest.addSort(new Sort(sort));
        }
        if (instanceId != null && instanceId > 0) {
            listRequest.addRestriction(new ObjectRestriction(Rule.EQ, "instance.id", instanceId));
        }
        //
        final ListResponse<InstanceWorkerPluginConfig> listResponse = instanceWorkerPluginLogic.getInstanceWorkerPluginConfigs(listRequest);
        modelAndView.addObject("listResponse", listResponse);
        return modelAndView;
    }

    @RequestMapping(value = "/instancenotifier/create", method = RequestMethod.GET)
    public ModelAndView getCreate(@RequestParam(value = "instanceId", required = false) final Long instanceId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-instance-notifier-manager");
        final InstanceWorkerPluginConfig instanceWorkerPluginConfig = new InstanceWorkerPluginConfig();
        final Instance instance = new Instance();
        if (instanceId != null && instanceId >= 0) {
            instance.setId(instanceId);
        }
        instanceWorkerPluginConfig.setInstance(instance);
        modelAndView.addObject("instanceNotifier", instanceWorkerPluginConfig);
        setRequestAttributes(modelAndView);
        return modelAndView;
    }

    private void setRequestAttributes(final ModelAndView modelAndView) {
        modelAndView.addObject("stateTypes", StateType.values());
        modelAndView.addObject("instances", instanceLogic.getInstances());
        modelAndView.addObject("postWorkerPlugins", pluginLogic.getPluginClasses(PostWorkerPlugin.class));

    }

    @RequestMapping(value = "/instancenotifier/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@RequestParam("instanceNotifierId") final Long instanceNotifierId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-instance-notifier-manager");
        modelAndView.addObject("instanceNotifier", instanceWorkerPluginLogic.getInstanceWorkerPluginConfig(instanceNotifierId));
        setRequestAttributes(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/instancenotifier/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("instanceNotifier") final InstanceWorkerPluginConfig instanceWorkerPluginConfig, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-instance-notifier-manager");
        } else {
            instanceWorkerPluginLogic.createOrUpdate(instanceWorkerPluginConfig);
            modelAndView = new ModelAndView("redirect:/instancenotifier/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/instancenotifier/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("instanceNotifier") final InstanceWorkerPluginConfig notifier, final BindingResult bindingResult) {
        return handleFormCreate(notifier, bindingResult);
    }

    @RequestMapping(value = "/instancenotifier/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@RequestParam("instanceNotifierId") final Long instanceNotifierId) {
        final ModelAndView modelAndView = new ModelAndView("delete-instance-notifier-manager");
        modelAndView.addObject("instanceNotifier", instanceWorkerPluginLogic.getInstanceWorkerPluginConfig(instanceNotifierId));
        return modelAndView;
    }

    @RequestMapping(value = "/instancenotifier/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@ModelAttribute("instanceNotifier") final InstanceWorkerPluginConfig instanceWorkerPluginConfig, final BindingResult bindingResult) {
        instanceWorkerPluginLogic.delete(instanceWorkerPluginConfig.getId());
        return new ModelAndView("redirect:/instancenotifier/index.html");
    }

}
