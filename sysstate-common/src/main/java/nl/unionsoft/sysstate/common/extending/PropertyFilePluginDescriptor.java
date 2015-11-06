package nl.unionsoft.sysstate.common.extending;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.logic.ListOfValueLogic;
import nl.unionsoft.sysstate.common.util.PropertyGroupUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

public class PropertyFilePluginDescriptor implements PluginDescriptor<StateResolver, InstanceDto> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyFilePluginDescriptor.class);

    protected final ListOfValueLogic listOfValueLogic;
    
    protected Map<String, String> properties;
    
    public PropertyFilePluginDescriptor(ListOfValueLogic listOfValueLogic){
        this.listOfValueLogic = listOfValueLogic;
    }

    @Override
    public List<PropertyMetaValue> getPropertyMeta(StateResolver stateResolver) {

        Stack<Class<?>> classStack = new Stack<Class<?>>();
        Class<?> superClass = stateResolver.getClass();
        while (!Object.class.equals(superClass)) {
            classStack.push(superClass);
            superClass = superClass.getSuperclass();
        }

        List<PropertyMetaValue> propertyMetas = new ArrayList<PropertyMetaValue>();
        while (!classStack.empty()) {
            Class<?> stackClass = classStack.pop();
            addPropertyMetasFromPropertyFiles(propertyMetas, stackClass);

        }

        return propertyMetas;
    }

    protected void addPropertyMetasFromPropertyFiles(List<PropertyMetaValue> propertyMetas, Class<?> stackClass) {
        Map<String, Properties> instanceGroupProperties = PropertyGroupUtil.getGroupProperties(getPropertiesForClass(stackClass), "instance");
        for (Entry<String, Properties> entry : instanceGroupProperties.entrySet()) {
            String id = entry.getKey();
            Properties properties = entry.getValue();
            PropertyMetaValue propertyMetaValue = new PropertyMetaValue();
            propertyMetaValue.setId(id);
            propertyMetaValue.setTitle(properties.getProperty("title", id));
            propertyMetaValue.setDescription(properties.getProperty("description"));
            String lovResolver = properties.getProperty("resolver");
            if (StringUtils.isNotEmpty(lovResolver)) {
                propertyMetaValue.setLov(listOfValueLogic.getListOfValues(lovResolver, propertyMetaValue));
            }
            propertyMetas.add(propertyMetaValue);
        }
    }

    public Properties getPropertiesForClass(final Class<?> theClass) {
        LOG.debug("Getting properties for class '{}'.", theClass);
        String propertyResource = "/" + StringUtils.replace(theClass.getCanonicalName(), ".", "/") + ".properties";
        return getPropertiesFromResource(propertyResource);
    }

    private Properties getPropertiesFromResource(final String propertyResource) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = PropertyFilePluginDescriptor.class.getResourceAsStream(propertyResource);
            if (inputStream == null) {
                LOG.warn("No properties found for resource '{}'!", propertyResource);
            } else {
                LOG.info("Loading props from class resource at '{}'", propertyResource);
                properties.load(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        LOG.info("Result for getPropertiesFromResource: {}", properties);
        return properties;

    }

    @Override
    public void populate(InstanceDto from) {
        properties = from.getConfiguration();
    }

  

}
