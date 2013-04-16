package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.logic.InstanceLogic;
import nl.unionsoft.sysstate.queue.FetchStateWorker;
import nl.unionsoft.sysstate.queue.ReferenceWorker;
import nl.unionsoft.sysstate.queue.ReferenceWorkerImpl.Statistics;
import nl.unionsoft.sysstate.queue.ReferenceWorkerImpl.Task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class QueueController {

    @Inject
    @Named("referenceWorker")
    private ReferenceWorker referenceWorker;

    @Inject
    @Named("instanceLogic")
    private InstanceLogic instanceLogic;

    @RequestMapping(value = "/queue/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("queue-manager");
        final Statistics statistics = referenceWorker.getStatistics();
        modelAndView.addObject("statistics", statistics);
        modelAndView.addObject("tasks", statistics.getTasks());
        return modelAndView;
    }

    @RequestMapping(value = "/queue/stacktrace/{reference}", method = RequestMethod.GET)
    public ModelAndView stackTrace(@PathVariable(value = "reference") final String reference) {
        final ModelAndView modelAndView = new ModelAndView("queue-stacktrace");
        modelAndView.addObject("stackTrace", referenceWorker.getStackTrace(reference));
        return modelAndView;
    }

}
