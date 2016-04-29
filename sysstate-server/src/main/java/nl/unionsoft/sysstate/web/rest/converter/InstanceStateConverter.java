package nl.unionsoft.sysstate.web.rest.converter;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.sysstate.common.dto.InstanceStateDto;
import nl.unionsoft.sysstate.sysstate_1_0.Instance;

@Service("instanceStateConverter")
public class InstanceStateConverter implements Converter<Instance, InstanceStateDto> {

    @Inject
    @Named("restInstanceConverter")
    private InstanceConverter instanceConverter;

    @Inject
    @Named("restStateConverter")
    private StateConverter stateConverter;

    @Override
    public Instance convert(InstanceStateDto instanceState) {
        if (instanceState == null) {
            return null;
        }
        Instance instance = instanceConverter.convert(instanceState.getInstance());
        if (instance != null) {
            instance.setState(stateConverter.convert(instanceState.getState()));
        }
        return instance;
    }

}
