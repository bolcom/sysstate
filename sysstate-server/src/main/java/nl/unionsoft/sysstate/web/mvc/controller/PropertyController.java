package nl.unionsoft.sysstate.web.mvc.controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import nl.unionsoft.sysstate.common.logic.PropertyLogic;
import nl.unionsoft.sysstate.domain.Property;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class PropertyController {
    @Inject
    @Named("propertyLogic")
    private PropertyLogic propertyLogic;

    //    @RequestMapping(value = "/property/index", method = RequestMethod.GET)
    //    public ModelAndView list() {
    //        final ModelAndView modelAndView = new ModelAndView("list-property-manager"); // no
    //                                                                                     // .jsp
    //                                                                                     // here
    //        modelAndView.addObject("properties", propertyLogic.getProperties());
    //        return modelAndView;
    //    }
    //
    //    @RequestMapping(value = "/property/create", method = RequestMethod.GET)
    //    public ModelAndView getCreate() {
    //        final ModelAndView modelAndView = new ModelAndView("create-update-property-manager"); // no
    //                                                                                              // .jsp
    //                                                                                              // here
    //        modelAndView.addObject("property", new Property());
    //        return modelAndView;
    //    }
    //
    //    @RequestMapping(value = "/property/update", method = RequestMethod.GET)
    //    public ModelAndView getUpdate(@RequestParam("id") final String id) {
    //        final ModelAndView modelAndView = new ModelAndView("create-update-property-manager"); // no
    //                                                                                              // .jsp
    //                                                                                              // here
    //
    //        modelAndView.addObject("property", getProperty(id));
    //        return modelAndView;
    //    }
    //
    //    private Property getProperty(final String id) {
    //        final Property property = new Property();
    //        property.setKey(id);
    //        property.setValue(propertyLogic.getProperty(id, null));
    //        return property;
    //    }

    @RequestMapping(value = "/property/create", method = RequestMethod.POST)
    public ModelAndView handleFormCreate(@Valid @ModelAttribute("property") final Property property, final BindingResult bindingResult) {

        ModelAndView modelAndView = null;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("create-update-property-manager");
        } else {
            propertyLogic.setProperty(property.getKey(), property.getValue());
            modelAndView = new ModelAndView("redirect:/property/index.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/property/update", method = RequestMethod.POST)
    public ModelAndView handleFormUpdate(@Valid @ModelAttribute("property") final Property property, final BindingResult bindingResult) {
        return handleFormCreate(property, bindingResult);
    }

    //    @RequestMapping(value = "/property/delete", method = RequestMethod.GET)
    //    public ModelAndView getDelete(@RequestParam("id") final String id) {
    //        final ModelAndView modelAndView = new ModelAndView("delete-property-manager"); // no
    //        // .jsp
    //        // here
    //        modelAndView.addObject("property", getProperty(id));
    //        return modelAndView;
    //    }

    @RequestMapping(value = "/property/delete", method = RequestMethod.POST)
    public ModelAndView handleDelete(@Valid @ModelAttribute("property") final Property property, final BindingResult bindingResult) {
        propertyLogic.setProperty(property.getKey(), null);
        return new ModelAndView("redirect:/property/index.html");
    }

}
