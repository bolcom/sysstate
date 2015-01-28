package nl.unionsoft.sysstate.web.rest.converter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.dto.ViewResultDto;
import nl.unionsoft.sysstate.sysstate_1_0.EcoSystem;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;
import nl.unionsoft.sysstate.sysstate_1_0.InstanceList;
import nl.unionsoft.sysstate.sysstate_1_0.State;

import org.springframework.stereotype.Service;

@Service("restEcoSystemConverter")
public class EcoSystemConverter implements Converter<EcoSystem, ViewResultDto>{

    @Inject
    @Named("restInstanceConverter")
    private Converter<Instance, InstanceDto> instanceConverter;
    
    @Override
    public EcoSystem convert(ViewResultDto dto) {
        if (dto == null){
            return null;
        }
        EcoSystem ecoSystem = new EcoSystem();
        InstanceList instanceList = new InstanceList();
        
        List<InstanceDto> instances = dto.getInstances();
        if (instances != null){
            for (InstanceDto instance : instances){
                instanceList.getInstances().add(instanceConverter.convert(instance));
            }
        }
        ecoSystem.setInstanceList(instanceList);
        return ecoSystem;
    }

}
