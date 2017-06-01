package nl.unionsoft.sysstate.dao.impl.lov;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.PropertyMetaValue;
import nl.unionsoft.sysstate.common.extending.ListOfValueResolver;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.dao.impl.SwitchingStateDaoImpl;

@Service("stateDaoLovResolver")
public class StateDaoLovResolver implements ListOfValueResolver {

    private final Map<String, StateDao> stateDaos;

    @Inject
    public StateDaoLovResolver(Map<String, StateDao> stateDaos) {
        this.stateDaos = stateDaos;
    }

    @Override
    public Map<String, String> getListOfValues(PropertyMetaValue propertyMetaValue) {
        return stateDaos.entrySet().stream()
        .filter(entry -> !SwitchingStateDaoImpl.class.equals(entry.getValue().getClass()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getKey));
    }

}
