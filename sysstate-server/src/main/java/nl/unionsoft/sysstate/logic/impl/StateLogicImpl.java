package nl.unionsoft.sysstate.logic.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.common.converter.Converter;
import nl.unionsoft.common.list.model.ListRequest;
import nl.unionsoft.common.list.model.ListResponse;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.plugins.StateResolverPlugin;
import nl.unionsoft.sysstate.common.util.StateUtil;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.StateDao;
import nl.unionsoft.sysstate.domain.State;
import nl.unionsoft.sysstate.logic.PluginLogic;
import nl.unionsoft.sysstate.logic.StateLogic;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("stateLogic")
public class StateLogicImpl implements StateLogic {

    private static final Logger LOG = LoggerFactory.getLogger(StateLogicImpl.class);
    @Inject
    @Named("stateDao")
    private StateDao stateDao;

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    @Inject
    @Named("stateConverter")
    private Converter<StateDto, State> stateConverter;

    public void clean() {
        stateDao.clean();
    }

    public ListResponse<State> getStates(final ListRequest listRequest) {
        return stateDao.getStates(listRequest);
    }

    public void createOrUpdate(StateDto dto) {
        final InstanceDto instance = dto.getInstance();
        if (instance != null) {
            final Long instanceId = instance.getId();
            State state = stateDao.getLastStateForInstance(instance.getId());
            final Date stateDate = dto.getCreationDate().toDate();
            if (!match(dto, state)) {
                //@formatter:off
                LOG.info("State '{}' for instance '{}' changed! old='{}', new='{}'", new Object[] {
                        state, instanceId,dto, state });
                //@formatter:on
                state = new State();
                state.setDescription(dto.getDescription());
                state.setInstance(instanceDao.getInstance(instanceId));
                state.setCreationDate(stateDate);
                state.setState(dto.getState());
            } else {
                LOG.info("State '{}' for instance '{}' hasn't changed, updating timestamps, rating & messages only...", state, instance);
            }
            state.setMessage(dto.getMessage());
            state.setResponseTime(dto.getResponseTime());
            state.setLastUpdate(stateDate);
            state.setRating(dto.getRating());
            stateDao.createOrUpdate(state);
            dto.setId(state.getId());
        }
    }

    public StateDto requestStateForInstance(InstanceDto instance) {
        final StateDto state = new StateDto();
        state.setInstance(instance);

        if (instance.isEnabled()) {
            final String pluginClass = instance.getPluginClass();

            final Long now = System.currentTimeMillis();
            try {
                final StateResolverPlugin stateResolver = pluginLogic.getPlugin(pluginClass);
                if (stateResolver == null) {
                    throw new IllegalStateException("No stateResolver found for type '" + pluginClass + "'");
                }
                final InstanceDto instanceDto = new InstanceDto();
                instanceDto.setConfiguration(instance.getConfiguration());
                instanceDto.setId(instance.getId());
                stateResolver.setState(instanceDto, state);
                if (state.getState() == null) {
                    throw new IllegalStateException("Result has no state!");
                }
            } catch(final RuntimeException e) {
                LOG.warn("Unable to determine state for stateResolver in instance '{}' failed, caught RuntimeException!", instance, e);
                state.setState(StateType.ERROR);
                state.setDescription(e.getMessage());
                state.setResponseTime(0L);
                state.appendMessage(StateUtil.exceptionAsMessage(e));

            } finally {
                final Long responseTime = System.currentTimeMillis() - now;
                if (state.getResponseTime() < responseTime) {
                    state.setResponseTime(responseTime);
                }
            }
        } else {
            state.setState(StateType.DISABLED);
            state.setDescription("DISABLED");
            state.setResponseTime(0L);
        }

        final DateTime pollDate = new DateTime();
        state.setCreationDate(pollDate);
        return state;
    }

    public StateDto requestState(String pluginClass, String configuration) {
        final StateDto state = new StateDto();

        final Long now = System.currentTimeMillis();
        try {
            final StateResolverPlugin stateResolver = pluginLogic.getPlugin(pluginClass);
            if (stateResolver == null) {
                throw new IllegalStateException("No stateResolver found for type '" + pluginClass + "'");
            }
            final InstanceDto instanceDto = new InstanceDto();
            instanceDto.setConfiguration(configuration);
            stateResolver.setState(instanceDto, state);
            if (state.getState() == null) {
                throw new IllegalStateException("Result has no state!");
            }
        } catch(final RuntimeException e) {
            LOG.warn("Unable to determine state, caught RuntimeException!", e);
            state.setState(StateType.ERROR);
            state.setDescription(e.getMessage());
            state.setResponseTime(0L);
            state.appendMessage(StateUtil.exceptionAsMessage(e));

        } finally {
            final Long responseTime = System.currentTimeMillis() - now;
            if (state.getResponseTime() < responseTime) {
                state.setResponseTime(responseTime);
            }
        }

        final DateTime pollDate = new DateTime();
        state.setCreationDate(pollDate);
        return state;
    }

    public static boolean match(StateDto dto, State state) {

        boolean result = false;
        if (state != null) {
            result = StringUtils.equals(dto.getDescription(), state.getDescription()) && dto.getState() == state.getState();

        }
        return result;

    }

    public static boolean match(StateDto thisState, StateDto otherState) {

        boolean result = false;
        if (otherState != null) {
            result = StringUtils.equals(thisState.getDescription(), otherState.getDescription()) && thisState.getState() == otherState.getState();

        }
        return result;

    }

    public StateDto getLastStateForInstance(Long instanceId) {
        return stateConverter.convert(stateDao.getLastStateForInstance(instanceId));
    }

}
