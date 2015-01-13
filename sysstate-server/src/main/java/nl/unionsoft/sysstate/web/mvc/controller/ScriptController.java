package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.extending.ScriptExecutionResult;
import nl.unionsoft.sysstate.common.logic.ScriptLogic;
import nl.unionsoft.sysstate.web.mvc.form.ScriptExecutorForm;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class ScriptController {

    private ScriptLogic scriptLogic;

    @Inject
    public ScriptController(ScriptLogic scriptLogic) {
        this.scriptLogic = scriptLogic;
    }

    @RequestMapping(value = "/script/index", method = RequestMethod.GET)
    public ModelAndView home() {
        final ModelAndView modelAndView = new ModelAndView("script-manager");
        modelAndView.addObject("scriptExecutorNames", scriptLogic.getScriptExecutorNames());
        modelAndView.addObject("scriptExecutorForm", new ScriptExecutorForm());
        return modelAndView;
    }

    @RequestMapping(value = "/script/index", method = RequestMethod.POST)
    public ModelAndView execute(@Valid @ModelAttribute("scriptExecutorForm") final ScriptExecutorForm scriptExecutorForm, final BindingResult bindingResult) {
        final ModelAndView modelAndView = new ModelAndView("script-manager");
        modelAndView.addObject("scriptExecutorNames", scriptLogic.getScriptExecutorNames());
        modelAndView.addObject("scriptExecutorForm", scriptExecutorForm);
        
        ScriptExecutionResult scriptExecutionResult = scriptLogic.execute(scriptExecutorForm.getContents(), scriptExecutorForm.getExecutorName());
        modelAndView.addObject("output", scriptExecutionResult.getOutput());
        modelAndView.addObject("executionResult", scriptExecutionResult.getExecutionResult());
        modelAndView.addObject("exception", scriptExecutionResult.getException());
        
        return modelAndView;
    }

}
