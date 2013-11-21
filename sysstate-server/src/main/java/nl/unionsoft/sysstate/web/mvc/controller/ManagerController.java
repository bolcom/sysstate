package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import nl.unionsoft.sysstate.common.dto.FilterDto;
import nl.unionsoft.sysstate.common.logic.StatisticsLogic;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ManagerController {

    @Inject
    @Named("statisticsLogic")
    private StatisticsLogic statisticsLogic;

    @RequestMapping(value = "/manager/index.html", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("home-manager");
        modelAndView.addObject("statistics", statisticsLogic.getStatistics());
        return modelAndView;
    }

    @RequestMapping(value = "/manager/search.html", method = RequestMethod.POST)
    public ModelAndView search(@RequestParam("search") final String search, @RequestParam("where") final String where, final HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/manager/index.html");

        if (StringUtils.equalsIgnoreCase(where, "instances")) {
            final FilterDto filter = new FilterDto();
            filter.setSearch(search);
            FilterController.setFilter(session, filter);
            modelAndView = new ModelAndView("redirect:/filter/index.html");
        }
        return modelAndView;
    }

}
