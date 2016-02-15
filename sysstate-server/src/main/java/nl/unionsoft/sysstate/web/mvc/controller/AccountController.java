package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class AccountController {

    @Inject
    @Named("userLogic")
    private UserLogic userLogic;

    @RequestMapping(value = "/account/index", method = RequestMethod.GET)
    public ModelAndView index() {

        Optional<UserDto> currentUser = userLogic.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new IllegalStateException("Not logged in!");
        }
        
        final ModelAndView modelAndView = new ModelAndView("account-manager");
        modelAndView.addObject("user", currentUser.get());
        return modelAndView;
    }
}
