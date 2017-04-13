package nl.unionsoft.sysstate.logic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.common.util.PropertyGroupUtil;
import nl.unionsoft.sysstate.logic.PropertyMetaLogic;

@Named("propertyMetaLogic")
public class PropertyMetaLogicImpl implements PropertyMetaLogic {

    private static final Logger logger = LoggerFactory.getLogger(PropertyMetaLogicImpl.class);

    private final ApplicationContext applicationContext;

    @Inject
    public PropertyMetaLogicImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Cacheable("propertyMetaValueClassCache")
    public List<PropertyMetaValue> getPropertyMetasForClass(Class<?> componentClass) {

        Stack<Class<?>> classStack = createClassStack(componentClass);
        List<PropertyMetaValue> propertyMetas = new ArrayList<PropertyMetaValue>();
        while (!classStack.empty()) {
            Class<?> stackClass = classStack.pop();
            propertyMetas.addAll(addPropertyMetasFromPropertyFiles(stackClass));

        }
        propertyMetas.sort((pmv1, pmv2) -> pmv1.getOrder().compareTo(pmv2.getOrder()));
        return propertyMetas;
    }

    @Override
    @Cacheable("propertyMetaValueBeanCache")
    public List<PropertyMetaValue> getPropertyMetasForBean(String beanName) {
        return getPropertyMetasForClass(applicationContext.getBean(beanName).getClass());
    }

    private Stack<Class<?>> createClassStack(Class<?> componentClass) {
        Stack<Class<?>> classStack = new Stack<Class<?>>();
        Class<?> superClass = componentClass;
        while (!Object.class.equals(superClass)) {
            classStack.push(superClass);
            superClass = superClass.getSuperclass();
        }
        return classStack;
    }

    private List<PropertyMetaValue> addPropertyMetasFromPropertyFiles(Class<?> stackClass) {
        return PropertyGroupUtil.getGroupProperties(getPropertiesForClass(stackClass), "instance").entrySet().stream()
                .map(entry -> createPropertyMetaValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

    }

    public Properties getPropertiesForClass(final Class<?> theClass) {
        logger.debug("Getting properties for class '{}'.", theClass);
        String propertyResource = "/" + StringUtils.replace(theClass.getCanonicalName(), ".", "/") + ".properties";
        return getPropertiesFromResource(propertyResource);
    }

    private Properties getPropertiesFromResource(final String propertyResource) {
        Properties properties = new Properties();
        try (InputStream inputStream = PluginLogicImpl.class.getResourceAsStream(propertyResource);) {
            if (inputStream == null) {
                logger.warn("No properties found for resource '{}'!", propertyResource);
            } else {
                logger.info("Loading props from class resource at '{}'", propertyResource);
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read properties from resource for propertyResource [{" + propertyResource + "}]");
        }
        logger.info("Result for getPropertiesFromResource: {}", properties);
        return properties;

    }

    private PropertyMetaValue createPropertyMetaValue(String id, Properties properties) {
        String title = properties.getProperty("title", id);
        String value = properties.getProperty("default");
        Integer order = Integer.valueOf(properties.getProperty("order", "100"));
        Boolean nullable = BooleanUtils.toBoolean(properties.getProperty("nullable"));
        System.out.println(title + ": " + nullable);
        PropertyMetaValue propertyMetaValue = new PropertyMetaValue(id, title, nullable, value, order);
        String lovResolver = properties.getProperty("resolver");
        if (StringUtils.isNotEmpty(lovResolver)) {
            ListOfValueResolver listOfValueResolver = applicationContext.getBean(lovResolver, ListOfValueResolver.class);
            propertyMetaValue.getLov().putAll(listOfValueResolver.getListOfValues(propertyMetaValue));
        }
        return propertyMetaValue;
    }

}
