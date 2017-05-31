package nl.unionsoft.sysstate.web.rest.converter;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.common.dto.ProjectDto;
import nl.unionsoft.sysstate.common.dto.ProjectEnvironmentDto;
import nl.unionsoft.sysstate.sysstate_1_0.Environment;
import nl.unionsoft.sysstate.sysstate_1_0.Project;
import nl.unionsoft.sysstate.sysstate_1_0.ProjectEnvironment;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("restEnvironmentConverter")
public class EnvironmentConverter implements Converter<Environment, EnvironmentDto>{

    @Override
    public Environment convert(EnvironmentDto dto) {
        if (dto == null){
            return null;
        }
        Environment environment = new Environment();
        environment.setId(dto.getId());
        environment.setName(dto.getName());
        environment.setOrder(dto.getOrder());
        
        if (StringUtils.isNotEmpty(dto.getTags())){
            environment.getTags().addAll(Arrays.asList(StringUtils.split(dto.getTags(), " ")));    
        }        
        return environment;
    }

}
