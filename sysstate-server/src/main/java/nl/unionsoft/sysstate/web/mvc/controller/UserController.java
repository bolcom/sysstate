package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.domain.User;
import nl.unionsoft.sysstate.dto.MessageDto;
import nl.unionsoft.sysstate.dto.UserDto;
import nl.unionsoft.sysstate.logic.MessageLogic;
import nl.unionsoft.sysstate.logic.UserLogic;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class UserController {

    @Inject
    @Named("userLogic")
    private UserLogic userLogic;
    @Inject
    @Named("messageLogic")
    private MessageLogic messageLogic;

    @RequestMapping(value = "/user/index", method = RequestMethod.GET)
    public ModelAndView list() {
        final ModelAndView modelAndView = new ModelAndView("list-user-manager");
        modelAndView.addObject("users", userLogic.getUsers());
        return modelAndView;
    }

    @RequestMapping(value = "/user/create", method = RequestMethod.GET)
    public ModelAndView getCreate() {
        final ModelAndView modelAndView = new ModelAndView("create-update-user-manager");
        modelAndView.addObject("user", new User());
        addCommonObjects(modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "/user/{userId}/update", method = RequestMethod.GET)
    public ModelAndView getUpdate(@PathVariable("userId") final Long userId) {
        final ModelAndView modelAndView = new ModelAndView("create-update-user-manager");
        final UserDto user = userLogic.getUser(userId);
        modelAndView.addObject("user", user);
        addCommonObjects(modelAndView);
        return modelAndView;
    }

    private void addCommonObjects(final ModelAndView modelAndView) {
    
        modelAndView.addObject("roles", userLogic.getRoles());
    }

    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("user") final UserDto user, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            addCommonObjects(modelAndView);
            modelAndView = new ModelAndView("create-update-user-manager");
        } else {
            userLogic.createOrUpdate(user);
            modelAndView = new ModelAndView("redirect:/user/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/user/{userId}/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("user") final UserDto user, final BindingResult bindingResult) {
        return handleFormCreate(user, bindingResult);
    }

    @RequestMapping(value = "/user/{userId}/delete", method = RequestMethod.GET)
    public ModelAndView getDelete(@PathVariable("userId") final Long userId) {
        final ModelAndView modelAndView = new ModelAndView("delete-user-manager");
        modelAndView.addObject("user", userLogic.getUser(userId));
        return modelAndView;
    }

    @RequestMapping(value = "/user/{userId}/delete", method = RequestMethod.POST)
    public ModelAndView postDelete(@PathVariable("userId") final Long userId) {

        try {
            userLogic.delete(userId);
            messageLogic.addUserMessage(new MessageDto("Deleted user succesfully!", MessageDto.GREEN));
        } catch(final RuntimeException e) {
            messageLogic.addUserMessage(new MessageDto("Unable to delete user!", MessageDto.RED));
        }

        return new ModelAndView("redirect:/user/index.html");
    }

}
