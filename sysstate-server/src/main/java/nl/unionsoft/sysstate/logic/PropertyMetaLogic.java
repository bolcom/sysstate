package nl.unionsoft.sysstate.logic;

import java.util.List;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;

public interface PropertyMetaLogic {
    public List<PropertyMetaValue> getPropertyMetasForClass(Class<?> componentClass);

    public List<PropertyMetaValue> getPropertyMetasForBean(String beanName);
}
