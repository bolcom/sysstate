package nl.unionsoft.sysstate.logic.impl;

import nl.unionsoft.sysstate.common.extending.StateResolver;
import nl.unionsoft.sysstate.logic.StateResolverLogic;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("stateResolverLogic")
public class StateResolverLogicImpl implements StateResolverLogic, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public StateResolver getStateResolver(final String name) {
        StateResolver result = null;
        try {
            Class<?> beanClass = Class.forName(name);
            result = (StateResolver) applicationContext.getBean(beanClass);
        } catch (ClassNotFoundException e) {
            result = applicationContext.getBean(name, StateResolver.class);
        }
        return result;
    }

    public String[] getStateResolverNames() {
        return applicationContext.getBeanNamesForType(StateResolver.class);
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
