package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.enums.FilterBehaviour;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.InstanceStateLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.dto.UserDto.Role;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

@Controller()
public class FilterController {

    public static final String REDIR_PATH = "/filter/index.html";

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @Inject
    @Named("stateResolverLogic")
    private StateResolverLogic stateResolverLogic;

    @Inject
    @Named("filterLogic")
    private FilterLogic filterLogic;

    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @Inject
    @Named("projectLogic")
    private ProjectLogic projectLogic;

    @Inject
    @Named("environmentLogic")
    private EnvironmentLogic environmentLogic;

    @Inject
    private InstanceStateLogic instanceStateLogic;

    @RequestMapping(value = "/filter/index", method = RequestMethod.GET)
    public ModelAndView index(@Valid @ModelAttribute("filter") final FilterDto filter, 
            @RequestParam(required = false, value = "action") String action,
            HttpServletRequest httpServletRequest) {
       
        if (StringUtils.equals("save", action) && saveAllowed(httpServletRequest)) {
            filterLogic.createOrUpdate(filter);
            messageLogic.addUserMessage(new MessageDto("Filter saved as '" + filter.getName() + "'.", MessageDto.GREEN));
            return filterRedirectModelAndView(filter);
        } else {
            ModelAndView modelAndView = new ModelAndView("search-filter-manager");
            modelAndView.addObject("instanceStates", instanceStateLogic.getInstanceStates(filter, FilterBehaviour.DIRECT));
            modelAndView.addObject("filter", filter);
            modelAndView.addObject("stateResolvers", stateResolverLogic.getStateResolverNames());
            modelAndView.addObject("states", StateType.values());
            modelAndView.addObject("environments", environmentLogic.getEnvironments());
            modelAndView.addObject("projects", projectLogic.getProjects());
            return modelAndView;            
        }

    }
    
    private boolean saveAllowed(HttpServletRequest httpServletRequest){
        return httpServletRequest.isUserInRole(Role.ADMIN.prefixedRole()) ||  httpServletRequest.isUserInRole(Role.EDITOR.prefixedRole());
    }

    @RequestMapping(value = "/filter/load/{filterId}/index.html", method = RequestMethod.GET)
    public ModelAndView userFilter(@PathVariable("filterId") final Long filterId, RedirectAttributes redirectAttrs) {
        Optional<FilterDto> optFilter =filterLogic.getFilter(filterId);
        if (!optFilter.isPresent()){
            throw new IllegalStateException("Requested filter with id [" + filterId + "] cannot be found!");
        }
        return filterRedirectModelAndView(optFilter.get());
    }

    private ModelAndView filterRedirectModelAndView(final FilterDto filter) {
        ModelAndView modelAndView = new ModelAndView(new RedirectView("/filter/index.html"));
        modelAndView.addObject("id", filter.getId());
        modelAndView.addObject("name", filter.getName());
        modelAndView.addObject("projects", filter.getProjects());
        modelAndView.addObject("environments", filter.getEnvironments());
        modelAndView.addObject("stateResolvers", filter.getStateResolvers());
        modelAndView.addObject("tags", filter.getTags());
        modelAndView.addObject("search", filter.getSearch());

        return modelAndView;
    }

    @RequestMapping(value = "/filter/{filterId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("filterId") final Long filterId) {
        final ModelAndView modelAndView = new ModelAndView("delete-filter-manager");
        
        Optional<FilterDto> optFilter = filterLogic.getFilter(filterId);
        if (!optFilter.isPresent()){
            messageLogic.addUserMessage(new MessageDto("Filter was already deleted.", MessageDto.GREEN));
            return new ModelAndView("redirect:/filter/list.html");    
        }
        modelAndView.addObject("filter", optFilter.get());
        return modelAndView;
    }
    
    @RequestMapping(value = "/filter/{filterId}/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@PathVariable("filterId") final Long filterId) {
        filterLogic.delete(filterId);
        return new ModelAndView("redirect:/filter/list.html");
    }

    @RequestMapping(value = "/filter/list", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-filter-manager");
        modelAndView.addObject("filters", filterLogic.getFilters());
        return modelAndView;
    }

}
