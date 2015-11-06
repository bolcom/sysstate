package nl.unionsoft.sysstate.logic.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.logic.ListOfValueLogic;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("listOfValueLogicImpl")
public class ListOfValueLogicImpl implements ListOfValueLogic {

    private final ApplicationContext applicationContext;

    @Inject
    public ListOfValueLogicImpl(@Named ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, String> getListOfValues(String lovResolver, PropertyMetaValue propertyMetaValue) {
        return applicationContext.getBean(lovResolver, ListOfValueResolver.class).getListOfValues(propertyMetaValue);
    }

    @Override
    public Map<String, String> getListOfValues(Class<? extends ListOfValueResolver> lovResolverClass, PropertyMetaValue propertyMetaValue) {
        return applicationContext.getBean(lovResolverClass).getListOfValues(propertyMetaValue);
    }

}
