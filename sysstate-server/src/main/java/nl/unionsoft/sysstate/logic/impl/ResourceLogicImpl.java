package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ApplicationContext;

import nl.unionsoft.common.converter.ListConverter;
import nl.unionsoft.sysstate.common.dto.ResourceDto;
import nl.unionsoft.sysstate.common.extending.ResourceManager;
import nl.unionsoft.sysstate.common.logic.ResourceLogic;
import nl.unionsoft.sysstate.converter.OptionalConverter;
import nl.unionsoft.sysstate.converter.ResourceConverter;
import nl.unionsoft.sysstate.dao.impl.ResourceDao;
import nl.unionsoft.sysstate.domain.Resource;
import nl.unionsoft.sysstate.plugins.http.HttpConstants;

@Named("resourceLogic")
public class ResourceLogicImpl implements ResourceLogic {

    private final ApplicationContext applicationContext;
    private final ResourceDao resourceDao;
    private final ResourceConverter resourceConverter;

    @Inject
    private ResourceLogicImpl(ApplicationContext applicationContext, ResourceDao resourceDao, ResourceConverter resourceConverter) {

        this.applicationContext = applicationContext;
        this.resourceDao = resourceDao;
        this.resourceConverter = resourceConverter;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResourceInstance(String resourceManager, String name) {

        Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(name, resourceManager);
        if (!optResource.isPresent()) {
            throw new IllegalStateException("No resource is defined for name [" + name + "] and manager [" + resourceManager + "]");
        }
        return (T) getResourceManager(resourceManager).getResource(resourceConverter.convert(optResource.get()));

    }

    @Override
    public List<String> getResourceNames(String resourceManager) {
        return ListConverter.convert(resourceConverter, resourceDao.getResourcesByManager(resourceManager))
                .stream().map(ResourceDto::getName)
                .collect(Collectors.toList());

    }

    @Override
    public void createOrUpdate(ResourceDto resourceDto) {
        Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(resourceDto.getName(), resourceDto.getManager());
        Resource resource = null;
        if (optResource.isPresent()) {
            resource = optResource.get();
        } else {
            resource = new Resource();
            resource.setManager(resourceDto.getManager());
            resource.setName(resourceDto.getName());
        }
        resource.setConfiguration(resourceDto.getConfiguration());
        getResourceManager(resource.getManager()).update(resourceDto);
        resourceDao.createOrUpdate(resource);

    }

    private ResourceManager<?> getResourceManager(String resourceManager) {
        return (ResourceManager<?>) applicationContext.getBean(resourceManager, ResourceManager.class);
    }

    @PostConstruct
    public void addDefaultResources() {
        applicationContext.getBeansOfType(ResourceManager.class).values().stream().forEach(rm -> {

            @SuppressWarnings("unchecked")
            List<ResourceDto> defaultResources = rm.getDefaultResources();
            defaultResources.stream().forEach(r -> {
                Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(r.getName(), r.getManager());
                if (!optResource.isPresent()) {
                    Resource resource = new Resource();
                    resource.setManager(r.getManager());
                    resource.setName(r.getName());
                    resource.setConfiguration(r.getConfiguration());
                    resourceDao.createOrUpdate(resource);
                }
            });
        });
    }

    @Override
    public void deleteResource(String resourceManager, String name) {

        Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(name, resourceManager);
        if (optResource.isPresent()) {
            resourceDao.delete(optResource.get().getId());
        }
        getResourceManager(resourceManager).remove(name);
    }

    @Override
    public Optional<ResourceDto> getResource(String resourceManager, String name) {
        return OptionalConverter.convert(resourceDao.getResourceByNameAndManager(name, resourceManager), resourceConverter);
    }

    @Override
    public List<ResourceDto> getResources() {
        return ListConverter.convert(resourceConverter, resourceDao.getResources());
    }

}
