package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.mchange.v1.util.ArrayUtils;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.logic.EnvironmentLogic;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.common.logic.ProjectLogic;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

@Controller()
public class FilterController {

    public static final String REDIR_PATH = "/filter/index.html";
    private static final String REDIRECT_FILTER_INDEX_HTML = "redirect:/filter/index.html";
    private static final String FILTER = "searchFilter";
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

    @RequestMapping(value = "/filter/index", method = RequestMethod.GET)
    //@formatter:off
    public ModelAndView index(
            @Valid @ModelAttribute("filter") final FilterDto filter
            ) {
        //@formatter:on        
        ModelAndView modelAndView = new ModelAndView("search-filter-manager");



        modelAndView.addObject("instances", instanceLogic.getInstances(filter));
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("stateResolvers", stateResolverLogic.getStateResolverNames());
        modelAndView.addObject("environments", environmentLogic.getEnvironments());
        modelAndView.addObject("projects", projectLogic.getProjects());
        return modelAndView;
    }

    @SafeVarargs
    public static <T> List<T> safeAsList(T... a) {
        if (a == null) {
            return new ArrayList<T>();
        }
        return Arrays.asList(a);

    }

    @RequestMapping(value = "/filter/index", method = RequestMethod.POST)
    public ModelAndView filter(@Valid @ModelAttribute("filter") final FilterDto filter, final BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView(new RedirectView("/filter/index.html", true));
        mav.addObject("project", filter.getProjects());
        mav.addObject("environment", filter.getEnvironments());
        mav.addObject("stateResolver", filter.getStateResolvers());
        mav.addObject("tags", filter.getStateResolvers());
        mav.addObject("search", filter.getSearch());
        
        return mav;
    }

    @RequestMapping(value = "/filter/load/{filterId}/index.html", method = RequestMethod.GET)
    public ModelAndView userFilter(@PathVariable("filterId") final Long filterId, final HttpSession session) {
        final FilterDto filter = filterLogic.getFilter(filterId);
        setFilter(session, filter);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/save", method = RequestMethod.POST)
    public ModelAndView saveFilter(@Valid @ModelAttribute("filter") final FilterDto saveFilter, final HttpSession session, final BindingResult bindingResult) {
        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            // TODO
            modelAndView = new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
        } else {
            final FilterDto filter = getFilter(session);
            final String filterName = saveFilter.getName();
            filter.setName(filterName);
            filterLogic.createOrUpdate(filter);
            messageLogic.addUserMessage(new MessageDto("FilterDto saved as '" + filterName + "'.", MessageDto.GREEN));
            modelAndView = new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/filter/{filterId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("filterId") final Long filterId) {
        final ModelAndView modelAndView = new ModelAndView("delete-filter-manager");
        modelAndView.addObject("filter", filterLogic.getFilter(filterId));
        return modelAndView;
    }

    @RequestMapping(value = "/filter/{filterId}/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@PathVariable("filterId") final Long filterId) {
        filterLogic.delete(filterId);
        return new ModelAndView("redirect:/filter/index.html");
    }

    @RequestMapping(value = "/filter/preset/{preset}.html", method = RequestMethod.GET)
    public ModelAndView filterPresset(@PathVariable("preset") final String preset, final HttpSession session) {

        final FilterDto filter = new FilterDto();
        setFilter(session, filter);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/list", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-filter-manager");
        modelAndView.addObject("filters", filterLogic.getFilters());
        return modelAndView;
    }

    private void filterProjectEnvironment(final FilterDto filter, final Long projectId, final Long environmentId) {
        if (projectId != null && projectId >= 0) {
            final List<Long> projects = filter.getProjects();
            projects.clear();
            projects.add(projectId);
        }
        if (environmentId != null && environmentId >= 0) {
            final List<Long> environments = filter.getEnvironments();
            environments.clear();
            environments.add(environmentId);
        }
    }

    private void mergeStateResolvers(final FilterDto filter, final FilterDto sessionFilter) {
        final List<String> newStateResolvers = filter.getStateResolvers();
        final List<String> sessionStateResolvers = sessionFilter.getStateResolvers();
        sessionStateResolvers.clear();
        if (newStateResolvers != null) {
            sessionStateResolvers.addAll(newStateResolvers);
        }

    }

    private void mergeEnvironments(final FilterDto filter, final FilterDto sessionFilter) {
        final List<Long> newEnvironments = filter.getEnvironments();
        final List<Long> sessionEnvironments = sessionFilter.getEnvironments();
        sessionEnvironments.clear();
        if (newEnvironments != null) {
            sessionEnvironments.addAll(newEnvironments);
        }
    }

    private void mergeProjects(final FilterDto filter, final FilterDto sessionFilter) {
        final List<Long> newProjects = filter.getProjects();
        final List<Long> sessionProjects = sessionFilter.getProjects();
        sessionProjects.clear();
        if (newProjects != null) {
            sessionProjects.addAll(newProjects);
        }
    }

    public static FilterDto getFilter(final HttpSession session) {
        FilterDto filter = (FilterDto) session.getAttribute(FILTER);
        if (filter == null) {
            filter = new FilterDto();
            session.setAttribute(FILTER, filter);
        }
        return filter;
    }

    public static void setFilter(final HttpSession session, final FilterDto filter) {
        session.setAttribute(FILTER, filter);
    }

}
