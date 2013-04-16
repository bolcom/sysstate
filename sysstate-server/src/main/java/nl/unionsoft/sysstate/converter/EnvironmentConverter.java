package nl.unionsoft.sysstate.converter;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.EnvironmentDto;
import nl.unionsoft.sysstate.domain.Environment;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("environmentConverter")
public class EnvironmentConverter implements Converter<EnvironmentDto, Environment> {

    public EnvironmentDto convert(Environment environment) {
        EnvironmentDto result = null;
        if (environment != null) {
            result = new EnvironmentDto();
            result.setId(environment.getId());
            result.setName(environment.getName());
            result.setOrder(environment.getOrder());
            result.setTags(environment.getTags());
            result.setDefaultInstanceTimeout(environment.getDefaultInstanceTimeout());
        }
        return result;
    }

}
