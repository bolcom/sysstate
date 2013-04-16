package nl.unionsoft.sysstate.web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    // @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    // public String printWelcome(ModelMap model, Principal principal) {
    // String name = principal.getName();
    // model.addAttribute("username", name);
    // model.addAttribute("message", "Spring Security Custom Form example");
    // return "hello";
    //
    // }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(ModelMap model) {
        return new ModelAndView("login");

    }

    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
    public ModelAndView loginerror(ModelMap model) {

        model.addAttribute("error", "true");
        return new ModelAndView("login");

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(ModelMap model) {
        return new ModelAndView("login");
    }
}
