package nl.unionsoft.sysstate.common.extending;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.unionsoft.sysstate.common.annotations.ParameterDefinition;
import nl.unionsoft.sysstate.common.converter.StringConverter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.logic.ListOfValueLogic;

import org.apache.commons.lang.StringUtils;

public class AnnotationsPluginDescriptor implements PluginDescriptor<StateResolver, InstanceDto> {

    protected final ListOfValueLogic listOfValueLogic;

    public AnnotationsPluginDescriptor(ListOfValueLogic listOfValueLogic) {
        this.listOfValueLogic = listOfValueLogic;
    }

    public List<PropertyMetaValue> getPropertyMeta(StateResolver stateResolver) {

        //@formatter:off
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter( f -> f.getAnnotation(ParameterDefinition.class) != null)
                .map(f -> createPropertyMetaValueFromField(f))
                .collect(Collectors.toList());
        //@formatter:on        
    }

    protected PropertyMetaValue createPropertyMetaValueFromField(Field f) {
        ParameterDefinition parameterDefinition = f.getAnnotation(ParameterDefinition.class);
        PropertyMetaValue propertyMetaValue = new PropertyMetaValue();
        propertyMetaValue.setId(f.getName());
        propertyMetaValue.setTitle(StringUtils.defaultIfEmpty(parameterDefinition.title(), f.getName()));
        propertyMetaValue.setDescription(parameterDefinition.description());
        propertyMetaValue.setNullable(parameterDefinition.nullable());
        Class<? extends ListOfValueResolver> lovResolverClass = parameterDefinition.lovResolver();
        if (!lovResolverClass.isInterface()) {
            propertyMetaValue.setLov(listOfValueLogic.getListOfValues(lovResolverClass, propertyMetaValue));
        }
        return propertyMetaValue;
    }

    @Override
    public void populate(InstanceDto from) {
        Map<String, String> configuration = from.getConfiguration();
        StringConverter stringConverter = new StringConverter();
        //@formatter:off
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter( f -> f.getAnnotation(ParameterDefinition.class) != null)
                .forEach(f -> setFieldValue(f, configuration.get(f.getName()), stringConverter));
        //@formatter:on    
    }

    protected void setFieldValue(Field field, String value, StringConverter stringConverter) {
        ParameterDefinition parameterDefinition = field.getAnnotation(ParameterDefinition.class);
        String finalValue = StringUtils.defaultIfEmpty(value, parameterDefinition.defaultValue());
        Object convertedFinalValue = stringConverter.convert(finalValue, field.getType());
        try {
            field.setAccessible(true);
            field.set(this, convertedFinalValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to set convertedFinalValue [" + convertedFinalValue + "] on field [" + field + "]", e);
        }
    }

}
