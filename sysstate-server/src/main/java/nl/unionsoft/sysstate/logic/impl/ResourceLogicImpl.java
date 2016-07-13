package nl.unionsoft.sysstate.logic.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public <T> T getResourceInstance(String resourceManager, String name) {

        Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(name, resourceManager);
        if (!optResource.isPresent()) {
            throw new IllegalStateException("No resource is defined for name [" + name + "] and manager [" + resourceManager + "]");
        }
        
        @SuppressWarnings("unchecked")
        ResourceManager<T> resourceFactory = (ResourceManager<T>) applicationContext.getBean(resourceManager, ResourceManager.class);// applicationContext.getBean(resourceManager, ResourceManager.class);
        return resourceFactory.getResource(resourceConverter.convert(optResource.get()));
        
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
        resourceDao.createOrUpdate(resource);

    }

    @Override
    public void deleteResource(String resourceManager, String name) {

        Optional<Resource> optResource = resourceDao.getResourceByNameAndManager(name, resourceManager);
        if (optResource.isPresent()) {
            resourceDao.delete(optResource.get().getId());
        }
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
