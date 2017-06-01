package nl.unionsoft.sysstate.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.Constants;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.spring.BeanConfiguration;

@Named("switchingStateDaoImpl")
public class SwitchingStateDaoImpl implements StateDao {

    private final Map<String, StateDao> stateDaos;
    private PluginLogic pluginLogic;

    @Inject
    public SwitchingStateDaoImpl(PluginLogic pluginLogic, Map<String, StateDao> stateDaos) {
        this.pluginLogic = pluginLogic;
        this.stateDaos = stateDaos;

    }

    @Override
    public void createOrUpdate(State state) {
        getDao().createOrUpdate(state);
    }

    @Override
    public Optional<State> getLastStateForInstance(Long instanceId) {
        return getDao().getLastStateForInstance(instanceId);

    }

    @Override
    public Optional<State> getLastStateForInstance(Long instanceId, StateType stateType) {
        return getDao().getLastStateForInstance(instanceId, stateType);
    }

    @Override
    public void cleanStatesOlderThanDays(int nrOfDays) {
        getDao().cleanStatesOlderThanDays(nrOfDays);
    }

    @Override
    public List<State> getStates() {
        return getDao().getStates();
    }

    private StateDao getDao() {
        Properties sysstateProperties = pluginLogic.getPluginProperties(Constants.SYSSTATE_PLUGIN_NAME);
        String stateDaoName = sysstateProperties.getProperty("stateDao", BeanConfiguration.MAP_STATE_DAO_IMPL);
        Optional<StateDao> optStateDao = Optional.ofNullable(stateDaos.get(stateDaoName));
        if (!optStateDao.isPresent()) {
            throw new IllegalStateException("No StateDao found with name [" + stateDaoName + "]");
        }
        return optStateDao.get();

    }

}
