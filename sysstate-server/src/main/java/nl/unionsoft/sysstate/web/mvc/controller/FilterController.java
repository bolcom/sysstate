package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.logic.FilterLogic;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/filter/index", method = RequestMethod.GET)
    public ModelAndView index(final HttpSession session) {
        final ModelAndView modelAndView = new ModelAndView("search-filter-manager");
        final FilterDto filter = getFilter(session);
        modelAndView.addObject("listResponse", instanceLogic.getInstances(filter));
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("states", StateType.values());
        modelAndView.addObject("stateResolvers", stateResolverLogic.getStateResolverNames());
        return modelAndView;
    }

    @RequestMapping(value = "/filter/project/{projectId}/index", method = RequestMethod.GET)
    public ModelAndView filterProject(final HttpSession session, @PathVariable("projectId") final Long projectId) {
        final FilterDto filter = getFilter(session);
        filterProjectEnvironment(filter, projectId, null);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/environment/{environmentId}/index", method = RequestMethod.GET)
    public ModelAndView filterEnvironment(final HttpSession session, @PathVariable("environmentId") final Long environmentId) {
        final FilterDto filter = getFilter(session);
        filterProjectEnvironment(filter, null, environmentId);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/project/{projectId}/environment/{environmentId}/index", method = RequestMethod.GET)
    public ModelAndView filterProjectEnvironment(final HttpSession session, @PathVariable("projectId") final Long projectId,
            @PathVariable("environmentId") final Long environmentId) {
        final FilterDto filter = getFilter(session);

        filterProjectEnvironment(filter, projectId, environmentId);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/index", method = RequestMethod.POST)
    public ModelAndView filter(@Valid @ModelAttribute("filter") final FilterDto filter, final HttpSession session) {
        final FilterDto sessionFilterDto = getFilter(session);
        mergeEnvironments(filter, sessionFilterDto);
        mergeProjects(filter, sessionFilterDto);
        mergeInstanceStates(filter, sessionFilterDto);
        mergeStateResolvers(filter, sessionFilterDto);
        sessionFilterDto.setSearch(filter.getSearch());
        sessionFilterDto.setTags(filter.getTags());
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/load/{filterId}/index.html", method = RequestMethod.GET)
    public ModelAndView userFilter(@PathVariable("filterId") final Long filterId, final HttpSession session) {
        final FilterDto filter = filterLogic.getFilter(filterId);
        setFilter(session, filter);
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/save", method = RequestMethod.POST)
    public ModelAndView saveFilter(@ModelAttribute("filter") final FilterDto saveFilter, final HttpSession session) {
        final FilterDto filter = getFilter(session);
        final String filterName = saveFilter.getName();
        filter.setName(filterName);
        filterLogic.createOrUpdate(filter);
        messageLogic.addUserMessage(new MessageDto("FilterDto saved as '" + filterName + "'.", MessageDto.GREEN));
        return new ModelAndView(REDIRECT_FILTER_INDEX_HTML);
    }

    @RequestMapping(value = "/filter/preset/{preset}.html", method = RequestMethod.GET)
    public ModelAndView filterPresset(@PathVariable("preset") final String preset, final HttpSession session) {

        final FilterDto filter = new FilterDto();
        if (StringUtils.equalsIgnoreCase(preset, "alerts")) {
            final List<StateType> prjEnvStates = filter.getStates();
            prjEnvStates.clear();
            prjEnvStates.add(StateType.UNSTABLE);
            prjEnvStates.add(StateType.ERROR);
        } else if (StringUtils.equalsIgnoreCase(preset, "stable")) {
            final List<StateType> instanceStates = filter.getStates();
            instanceStates.clear();
            instanceStates.add(StateType.STABLE);
        } else if (StringUtils.equalsIgnoreCase(preset, "unknown")) {
            final List<StateType> instanceStates = filter.getStates();
            instanceStates.clear();
            instanceStates.add(StateType.PENDING);
            instanceStates.add(StateType.DISABLED);
        }
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

    private void mergeInstanceStates(final FilterDto filter, final FilterDto sessionFilter) {
        final List<StateType> newStates = filter.getStates();
        final List<StateType> sessionStateType = sessionFilter.getStates();
        sessionStateType.clear();
        if (newStates != null) {
            sessionStateType.addAll(newStates);
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
